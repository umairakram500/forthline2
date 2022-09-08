/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fourthline.core.location.Coordinate
import com.fourthline.core.location.FourthlineLocationHelper
import com.fourthline.kyc.KycInfo
import com.fourthline.kyc.zipper.Zipper
import com.fourthline.sdksample.KycResultContainer
import com.fourthline.sdksample.R
import com.fourthline.sdksample.component.CommonStatusStepView
import com.fourthline.sdksample.databinding.ActivitySubmitKycBinding
import com.fourthline.sdksample.showDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.net.URI
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SubmitKycActivity : AppCompatActivity() {

    private val TAG: String = SubmitKycActivity::class.java.simpleName

    private lateinit var binding: ActivitySubmitKycBinding

    /**
     * References the Job/Coroutine that wraps the full submit process.
     *
     * Take notice that in this example, the Coroutine is launched on the Activity lifecycleScope,
     * meaning it will be cancelled onDestroy(). Using [androidx.work.WorkManager] is recommended
     * for executing long-running tasks.
     */
    private var submitKycJob: Job? = null

    /**
     * References the last appended [CommonStatusStepView], a custom view of which multiple will be
     * displayed to inform the end-user of the current step of the 'submit' process.
     */
    private var currentSubmitStepView: CommonStatusStepView? = null

    /**
     * Configure the Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySubmitKycBinding.inflate(layoutInflater).apply {
            buttonClose.setOnClickListener {
                finish()
            }
        }

        setContentView(binding.root)
    }

    /**
     * Start the 'submit' process if it hasn't started yet.
     */
    override fun onResume() {
        super.onResume()

        if (submitKycJob == null)
            submitKyc()
    }

    /**
     * Submit Kyc Verification request by bundling all collected data and uploading it.
     *
     * Take notice that in this example, the Coroutine is launched on the Activity lifecycleScope,
     * meaning it will be cancelled onDestroy(). Using [androidx.work.WorkManager] is recommended
     * for executing long-running tasks.
     */
    private fun submitKyc() {
        submitKycJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val kycInfo = createKyc()
                val validatedInfo = validateKyc(kycInfo)
                val zippedKyc = zipKyc(validatedInfo)
                val result = uploadKyc(zippedKyc)
                onKycSubmitted(result)
            } catch (exception: Exception) {
                handleSubmitKycException(exception)
            }
        }
    }

    /**
     * Create Kyc:
     * Get all the collected Kyc data (results from Scanners, together with additional data) and
     * create the result object, ready for validation.
     */
    private suspend fun createKyc(): KycInfo {
        addStatusStep(getString(R.string.submit_step_validating))
        val kycInfo = KycResultContainer.kycInfo.apply {
            metadata.location = requestLocation()
        }

        val validationErrors = kycInfo.validate()
        if (validationErrors.isNotEmpty()) {
            setCurrentStatusStep(status = CommonStatusStepView.UiStepStatus.FAILURE)

            // Throw an exception to handle gracefully.
            throw IllegalStateException(validationErrors.first().name)
        }

        return kycInfo
    }

    private suspend fun requestLocation() = suspendCoroutine<Coordinate> { continuation ->
        with(FourthlineLocationHelper.getLocationProvider(this)) {
            requestAuthorization({
                requestLocation({
                    continuation.resume(it)
                }, {
                    continuation.resumeWithException(it)
                })
            }, {
                continuation.resumeWithException(it)
            })
        }
    }

    /**
     * Validate Kyc:
     * With the created KycInfo, validate if the data passes the minimum requirements.
     */
    private suspend fun validateKyc(kycInfo: KycInfo): KycInfo {
        val validationErrors = kycInfo.validate()
        if (validationErrors.isNotEmpty()) {
            setCurrentStatusStep(status = CommonStatusStepView.UiStepStatus.FAILURE)

            // Throw an exception to handle gracefully.
            throw IllegalStateException(validationErrors.toString())
        }

        return kycInfo
    }

    /**
     * Zip Kyc:
     * After validation, create a Zip-file that wraps all collected data, images and videos.
     *
     * TODO: Test the blocking nature for zipping that it's not fully blocking the IO context.
     */
    private suspend fun zipKyc(kycInfo: KycInfo): URI {
        val zippedKyc = Zipper().createZipFile(kycInfo, this)

        setCurrentStatusStep(status = CommonStatusStepView.UiStepStatus.SUCCESS)

        return zippedKyc
    }

    /**
     * Upload Kyc:
     * Upload the Zipped Kyc data, stored on the local filesystem.
     *
     * Fourthline recommends to upload this to your own server using your preferred method.
     * After that a Server-2-Server call to submit the data to Fourthline for analysis can happen.
     */
    private suspend fun uploadKyc(zippedKyc: URI): Boolean {
        addStatusStep(getString(R.string.submit_step_uploading))

        // Simulate Kyc Upload
        delay(5000)

        setCurrentStatusStep(status = CommonStatusStepView.UiStepStatus.SUCCESS)

        // Assume a successful upload
        return true
    }

    /**
     * Once the KycInfo has been submitted, Fourthline will handle further verification.
     * Please refer to official documentation to learn how to retrieve verification status.
     */
    private suspend fun onKycSubmitted(uploadSucceeded: Boolean) = withContext(Main) {
        addStatusStep(
            getString(R.string.submit_step_finished),
            CommonStatusStepView.UiStepStatus.SUCCESS
        )
    }

    private suspend fun handleSubmitKycException(exception: Exception) = withContext(Main) {
        showDialog(
            title = getString(R.string.app_name),
            message = exception.message.toString(),
            positiveButtonText = getString(R.string.common_close),
            positiveButtonHandler = { finish() }
        )
    }

    /**
     * Convenience method to add a new status step to the UI.
     */
    private suspend fun addStatusStep(
        label: CharSequence,
        status: CommonStatusStepView.UiStepStatus = CommonStatusStepView.UiStepStatus.IN_PROGRESS
    ) = withContext(Main) {
        currentSubmitStepView = CommonStatusStepView(this@SubmitKycActivity).apply {
            setLabel(label)
            setStatus(status)

            binding.stepStatusContainer.addView(this)
        }
    }

    /**
     * Convenience method to update data for an existing status step.
     */
    private suspend fun setCurrentStatusStep(
        label: CharSequence? = null,
        status: CommonStatusStepView.UiStepStatus
    ) = withContext(Main) {
        currentSubmitStepView?.apply {
            if (label != null) {
                setLabel(label)
            }
            setStatus(status)
        }
    }

    /**
     * Convenience launcher for Activity.
     */
    companion object {
        fun start(context: Context) = with(context) {
            Intent(this, SubmitKycActivity::class.java).let(::startActivity)
        }
    }
}