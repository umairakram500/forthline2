/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fourthline.sdksample.*
import com.fourthline.sdksample.component.SelfieScannerOverlayView
import com.fourthline.sdksample.extensions.*
import com.fourthline.sdksample.helper.selfiescanner.*
import com.fourthline.vision.selfie.*

class SelfieFragment : SelfieScannerFragment() {

    private val TAG: String = SelfieFragment::class.java.simpleName

    /**
     * Creates and keeps an instance of custom view SelfieScannerOverlayView, which is passed to
     * the required method [SelfieScannerFragment.getOverlayView].
     *
     * The Fourthline SDK provides functionality for efficient scanning, while offering you the
     * flexibility to customize the experience to fit your product and identity.
     *
     * In this example application, the delegation pattern is used to give some more flexibility
     * to this view. This prevents some code-duplication as this view is used for both the
     * Activity and Fragment.
     */
    private val scannerOverlayView by lazy {
        SelfieScannerOverlayView(requireContext()).apply {
            configureButtons(onCloseScanner = {
                vibrate()
                finish()
            })
        }
    }

    /**
     * Overlay view for the Camera, containing buttons as well as guidance for the end-user.
     */
    override fun getOverlayView() = scannerOverlayView

    /**
     * Coordinates within the Camera Viewport used for Face Detection.
     */
    override fun getFaceDetectionArea() = scannerOverlayView.getFaceDetectionArea()

    /**
     * Configuration for the [SelfieScannerFragment].
     */
    override fun getConfig() = SelfieScannerConfig()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Clear previous Selfie result data on KycInfo
        KycResultContainer.selfieResult = null

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * You may enter [SelfieScannerStep.MANUAL_SELFIE] step when [SelfieScannerConfig.includeManualSelfiePolicy]
     * is set to TRUE and selfie step timeouts.
     * In this case present snapshot button to user and call [SelfieScannerFragment.takeSnapshot] on click.
     */
    override fun onStepUpdate(step: SelfieScannerStep) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Step update: $step")

        scannerOverlayView.onStepUpdate(step)
    }

    /**
     * Handle warnings during scanning, as returned by [SelfieScannerFragment].
     *
     * Informing the end-user will help them overcome any challenges, increasing the likeliness of
     * successful selfie scanning.
     */
    override fun onWarnings(warnings: List<SelfieScannerWarning>) = runOnUiThread {
        val prettyWarnings = warnings.joinToString(", ", "[", "]")
        Log.w(TAG, "Current warnings: $prettyWarnings")

        scannerOverlayView.onWarnings(warnings)
    }

    /**
     * Handle errors returned by [SelfieScannerFragment].
     */
    override fun onFail(error: SelfieScannerError) = runOnUiThread {
        vibrate()
        Log.e(TAG, "Scanner failed with error: $error")

        // In this example we don't handle interrupted errors due to the fragment being removed.
        if (!isRemoving) {
            showDialog(
                title = getString(R.string.common_failure),
                message = getString(error.getErrorLabel()),
                positiveButtonText = getString(R.string.common_try_again),
                positiveButtonHandler = { replaceWith(SelfieFragment()) },
                negativeButtonText = getString(R.string.common_dismiss),
                negativeButtonHandler = { finish() }
            )
        }
    }

    /**
     * Handle successful [SelfieScannerResult] returned by [SelfieScannerFragment].
     */
    override fun onSuccess(result: SelfieScannerResult) {
        vibrate()
        Log.e(TAG, "Selfie scan succeed")

        // Save results for later reference when submitting KycInfo for validation
        KycResultContainer.selfieResult = result

        SelfieResultActivity.start(requireContext())

        finish()
    }
}