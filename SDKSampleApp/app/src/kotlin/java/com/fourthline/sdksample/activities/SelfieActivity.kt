/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.fourthline.sdksample.*
import com.fourthline.sdksample.component.SelfieScannerOverlayView
import com.fourthline.sdksample.helper.selfiescanner.*
import com.fourthline.vision.selfie.*
class SelfieActivity : SelfieScannerActivity() {

    private val TAG: String = SelfieActivity::class.java.simpleName

    /**
     * Creates and keeps an instance of custom view SelfieScannerOverlayView, which is passed to
     * the required method [SelfieScannerActivity.getOverlayView].
     *
     * The Fourthline SDK provides functionality for efficient scanning, while offering you the
     * flexibility to customize the experience to fit your product and identity.
     *
     * In this example application, the delegation pattern is used to give some more flexibility
     * to this view. This prevents some code-duplication as this view is used for both the
     * Activity and Fragment.
     */
    private val scannerOverlayView by lazy {
        SelfieScannerOverlayView(this).apply {
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
     * Configuration for the [SelfieScannerActivity].
     */
    override fun getConfig() = SelfieScannerConfig()

    /**
     * Configure the Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Clear previous Selfie result data on KycInfo
        KycResultContainer.selfieResult = null
    }

    /**
     * You may enter [SelfieScannerStep.MANUAL_SELFIE] step when [SelfieScannerConfig.includeManualSelfiePolicy]
     * is set to TRUE and selfie step timeouts.
     * In this case present snapshot button to user and call [SelfieScannerActivity.takeSnapshot] on click.
     */
    override fun onStepUpdate(step: SelfieScannerStep) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Step update: $step")

        scannerOverlayView.onStepUpdate(step)
    }

    /**
     * Handle warnings during scanning, as returned by [SelfieScannerActivity].
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
     * Handle errors returned by [SelfieScannerActivity].
     */
    override fun onFail(error: SelfieScannerError) = runOnUiThread {
        vibrate()
        Log.e(TAG, "Scanner failed with error: $error")

        showDialog(
            title = getString(R.string.common_failure),
            message = getString(error.getErrorLabel()),
            positiveButtonText = getString(R.string.common_try_again),
            positiveButtonHandler = { recreate() },
            negativeButtonText = getString(R.string.common_dismiss),
            negativeButtonHandler = { finish() }
        )
    }

    /**
     * Handle successful [SelfieScannerResult] returned by [SelfieScannerActivity].
     */
    override fun onSuccess(result: SelfieScannerResult) {
        vibrate()
        Log.e(TAG, "Selfie scan succeed")

        // Save results for later reference when submitting KycInfo for validation
        KycResultContainer.selfieResult = result

        SelfieResultActivity.start(this)

        finish()
    }

    /**
     * Convenience launcher for Activity.
     */
    companion object {
        fun start(context: Context) = with(context) {
            Intent(this, SelfieActivity::class.java).let(::startActivity)
        }
    }
}