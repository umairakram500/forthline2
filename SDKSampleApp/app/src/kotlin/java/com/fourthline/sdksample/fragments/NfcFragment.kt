/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fourthline.nfc.*
import com.fourthline.nfc.NfcScannerSecurityKey.Companion.createWithMrtdData
import com.fourthline.sdksample.*
import com.fourthline.sdksample.R
import com.fourthline.sdksample.component.CommonStatusStepView
import com.fourthline.sdksample.databinding.ScannerUiNfcBinding
import com.fourthline.sdksample.extensions.*
import com.fourthline.sdksample.helper.nfcscanner.getErrorLabel
import com.fourthline.sdksample.helper.nfcscanner.getInstructionLabel
import com.fourthline.sdksample.helper.nfcscanner.getNfcSecurityKey
import java.util.*

class NfcFragment : NfcScannerFragment() {

    private val TAG: String = NfcFragment::class.java.simpleName

    private var _binding: ScannerUiNfcBinding? = null
    private val binding get() = _binding!!

    /**
     * References the last appended [CommonStatusStepView], a custom view of which multiple will be
     * displayed to inform the end-user of the current step of the [NfcScannerFragment].
     *
     * The Fourthline SDK provides functionality for efficient scanning, while offering you the
     * flexibility to customize the experience to fit your product and identity.
     */
    private var currentScannerStepView: CommonStatusStepView? = null

    /**
     * Create a Configuration for NfcScanner.
     *
     * In order to read data from official documents, first an authentication step needs to succeed.
     * In this example we first attempt to receive the necessary data from a previously completed
     * step, scanning the Document.
     *
     * Otherwise we default to example data for.
     */
    override fun getConfig(): NfcScannerConfig {
        val documentSecurityKey = KycResultContainer.documentResult?.getNfcSecurityKey()
            ?: getDefaultNfcSecurityKeyForFailure()

        return NfcScannerConfig(key = documentSecurityKey)
    }

    /**
     * Returns a NfcScannerSecurityKey, configured to fail scanning.
     *
     * Nfc reading requires an authentication step to pass, meaning configuration and document
     * need to match. Hardcoding your specific document values would allow for authentication to
     * pass and the scanner will proceed with reading.
     */
    private fun getDefaultNfcSecurityKeyForFailure() = createWithMrtdData(
        documentNumber = "ABC123456",
        birthDate = Date(),
        expiryDate = Date()
    )

    /**
     * Configure the Fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Clear previous Nfc result data on KycInfo
        KycResultContainer.nfcResult = null

        _binding = ScannerUiNfcBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Configure the View
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonClose.setOnClickListener {
            vibrate()
            finish()
        }
    }

    /**
     * Handle errors returned by [NfcScannerFragment].
     */
    override fun onFail(error: NfcScannerError) = runOnUiThread {
        vibrate()
        Log.e(TAG, "Scanner failed with error: $error")

        // Update UI
        currentScannerStepView?.apply {
            setStatus(CommonStatusStepView.UiStepStatus.FAILURE)
        }

        // Inform end-user
        // In this example we don't handle interrupted errors due to the fragment being removed.
        if (!isRemoving) {
            showDialog(
                title = getString(R.string.common_failure),
                message = getString(error.getErrorLabel()),
                positiveButtonText = getString(R.string.common_try_again),
                positiveButtonHandler = { replaceWith(NfcFragment()) },
                negativeButtonText = getString(R.string.common_dismiss),
                negativeButtonHandler = { finish() }
            )
        }
    }

    /**
     * Handle step updates returned by [NfcScannerFragment].
     *
     * Informing the end-user of what happens is especially important for attempting to read Nfc
     * data, due to unfamiliarity with where the chip is on the device and the given document.
     */
    override fun onStepUpdate(step: NfcScannerStep) = runOnUiThread {
        vibrate()
        Log.d(TAG, "Step update: $step")

        // Update last view
        setCurrentStatusStep(status = CommonStatusStepView.UiStepStatus.SUCCESS)

        // Append new view
        addStatusStep(
            getString(step.getInstructionLabel()),
            CommonStatusStepView.UiStepStatus.IN_PROGRESS
        )
    }

    /**
     * Convenience method to add a new status step to the UI.
     */
    private fun addStatusStep(
        label: CharSequence,
        status: CommonStatusStepView.UiStepStatus = CommonStatusStepView.UiStepStatus.IN_PROGRESS
    ) {
        currentScannerStepView = CommonStatusStepView(requireContext()).apply {
            setLabel(label)
            setStatus(status)

            binding.stepStatusContainer.addView(this)
        }
    }

    /**
     * Convenience method to update data for an existing status step.
     */
    private fun setCurrentStatusStep(
        label: CharSequence? = null,
        status: CommonStatusStepView.UiStepStatus
    ) {
        currentScannerStepView?.apply {
            if (label != null) {
                setLabel(label)
            }
            setStatus(status)
        }
    }

    /**
     * Handle successful [NfcScannerResult] returned by [NfcScannerFragment].
     */
    override fun onSuccess(result: NfcScannerResult) = runOnUiThread {
        vibrate()
        Log.d(TAG, "NFC scan succeed")

        // Save results for later reference when submitting KycInfo for validation
        KycResultContainer.nfcResult = result

        NfcResultActivity.start(requireContext())

        finish()
    }
}
