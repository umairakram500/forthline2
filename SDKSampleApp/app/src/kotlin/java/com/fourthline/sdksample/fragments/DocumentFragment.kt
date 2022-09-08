/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fourthline.core.DocumentType
import com.fourthline.core.DocumentType.PASSPORT
import com.fourthline.sdksample.DocumentResultActivity
import com.fourthline.sdksample.DocumentResultActivity.Companion.KEY_DOCUMENT_MRZ
import com.fourthline.sdksample.DocumentResultActivity.Companion.KEY_DOCUMENT_VIDEO_URI
import com.fourthline.sdksample.KycResultContainer
import com.fourthline.sdksample.R
import com.fourthline.sdksample.component.DocumentScannerOverlayView
import com.fourthline.sdksample.extensions.*
import com.fourthline.sdksample.helper.documentscanner.*
import com.fourthline.vision.document.*

class DocumentFragment : DocumentScannerFragment() {

    private val TAG: String = DocumentFragment::class.java.simpleName

    /**
     * Set the type of Document to that which was previously selected by the end-user, before
     * starting the scanner.
     */
    private val documentType: DocumentType by lazy { PASSPORT }

    /**
     * Creates and keeps an instance of custom view DocumentScannerOverlayView, which is passed to
     * the required method [DocumentScannerFragment.getOverlayView].
     *
     * The Fourthline SDK provides functionality for efficient scanning, while offering you the
     * flexibility to customize the experience to fit your product and identity.
     *
     * In this example application, the delegation pattern is used to give some more flexibility
     * to this view. This prevents some code-duplication as this view is used for both the
     * Activity and Fragment.
     */
    private val scannerOverlayView by lazy {
        DocumentScannerOverlayView(requireContext()).apply {
            configureButtons(
                documentScanner = this@DocumentFragment,
                onCloseScanner = { finish() }
            )
        }
    }

    /**
     * Overlay view for the Camera, containing buttons as well as guidance for the end-user.
     */
    override fun getOverlayView(): View = scannerOverlayView

    /**
     * Coordinates within the Camera Viewport used for Document Detection.
     *
     * In this example this is mapped to the coordinates of a specific mask, used for
     * the given documentType.
     */
    override fun getDocumentDetectionArea() = scannerOverlayView.getDocumentDetectionArea()

    /**
     * Configuration for the [DocumentScannerFragment].
     * Populate the 'type' (of document) to what was previously selected by the end-user.
     */
    override fun getConfig() = DocumentScannerConfig(type = documentType)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Clear previous Document result data on KycInfo
        KycResultContainer.documentResult = null

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Handle the total amount of steps for a DocumentScan.
     * For example, use this value to display the progress for the user.
     */
    override fun onStepsCountUpdate(count: Int) {
        Log.d(TAG, "Document step count has been updated to $count")
    }

    /**
     * Handle the start of new steps during scanning, as returned by [DocumentScannerFragment].
     *
     * Informing and assisting the end-user will help them understand what to do to succeed for the
     * step that was just started, increasing the likeliness of successful document scanning.
     */
    override fun onStepUpdate(step: DocumentScannerStep) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Step update: ${step.prettify()}")

        scannerOverlayView.onStepUpdate(step, documentType)
    }

    /**
     * Handle warnings during scanning, as returned by [DocumentScannerFragment].
     *
     * Informing the end-user will help them overcome any challenges, increasing the likeliness of
     * successful document scanning.
     */
    override fun onWarnings(warnings: List<DocumentScannerStepWarning>) = runOnUiThread {
        Log.w(TAG, "Current warnings: ${warnings.prettify()}")

        scannerOverlayView.onWarnings(warnings)
    }

    /**
     * Handle errors of individual steps that fail, as returned by [DocumentScannerFragment].
     */
    override fun onStepFail(error: DocumentScannerStepError) = runOnUiThread {
        vibrate()
        Log.e(TAG, "Scanner misuse, reason: $error")
        throw RuntimeException("Business logic violation, should never happen")
    }

    /**
     * Handle results of individual steps that succeed, as returned by [DocumentScannerFragment].
     */
    override fun onStepSuccess(result: DocumentScannerStepResult) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Step scan succeed")

        if (!scannerOverlayView.showIntermediateResult(result)) {
            moveToNextStep()
        }
    }

    /**
     * Handle errors returned by [DocumentScannerFragment].
     */
    override fun onFail(error: DocumentScannerError) = runOnUiThread {
        vibrate()
        Log.e(TAG, "Scanner failed with error: $error")

        // In this example we don't handle interrupted errors due to the fragment being removed.
        if (!isRemoving) {
            showDialog(
                title = getString(R.string.common_failure),
                message = getString(error.getErrorLabel()),
                positiveButtonText = getString(R.string.common_try_again),
                positiveButtonHandler = {
                    replaceWith(DocumentFragment())
                },
                negativeButtonText = getString(R.string.common_dismiss),
                negativeButtonHandler = {
                    finish()
                }
            )
        }
    }

    /**
     * Handle successful [DocumentScannerResult] returned by [DocumentScannerFragment].
     */
    override fun onSuccess(result: DocumentScannerResult) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Scan successful")

        // Save results for later reference when submitting KycInfo for validation
        KycResultContainer.documentResult = result

        DocumentResultActivity.start(
            requireContext(),
            Bundle().apply {
                putString(KEY_DOCUMENT_VIDEO_URI, result.videoRecording?.url?.path ?: "n/a")
                putString(KEY_DOCUMENT_MRZ, result.mrzInfo?.rawMrz ?: "n/a")
            }
        )

        finish()
    }
}
