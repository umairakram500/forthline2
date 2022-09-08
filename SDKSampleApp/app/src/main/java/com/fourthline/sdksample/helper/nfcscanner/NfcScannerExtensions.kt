package com.fourthline.sdksample.helper.nfcscanner

import androidx.annotation.StringRes
import com.fourthline.core.DocumentType
import com.fourthline.core.mrz.MrtdMrzInfo
import com.fourthline.nfc.NfcScannerError
import com.fourthline.nfc.NfcScannerError.*
import com.fourthline.nfc.NfcScannerSecurityKey
import com.fourthline.nfc.NfcScannerStep
import com.fourthline.nfc.NfcScannerStep.*
import com.fourthline.sdksample.R
import com.fourthline.vision.document.DocumentScannerResult

fun DocumentScannerResult.getNfcSecurityKey(): NfcScannerSecurityKey {
    return when (documentType) {
        DocumentType.DUTCH_DRIVERS_LICENSE -> NfcScannerSecurityKey.createWithIdlMrz(
            mrz = mrzInfo?.rawMrz ?: ""
        )
        else -> {
            when (val data = mrzInfo) {
                is MrtdMrzInfo -> {
                    NfcScannerSecurityKey.createWithMrtdData(
                        documentNumber = data.documentNumber,
                        birthDate = data.birthDate,
                        expiryDate = data.expirationDate
                    )
                }
                else -> null
            }
        }
    } ?: NfcScannerSecurityKey.createWithMrtdCanNumber(number = "")
}

@StringRes
fun NfcScannerStep.getInstructionLabel(): Int = when (this) {
    LOOKING_FOR_DOCUMENT -> R.string.nfc_scanner_step_looking_for_document
    CONNECTING_TO_DOCUMENT -> R.string.nfc_scanner_step_connecting_to_document
    AUTHENTICATING -> R.string.nfc_scanner_step_authenticating
    READING_DATA -> R.string.nfc_scanner_step_reading_data
}

@StringRes
fun NfcScannerError.getErrorLabel(): Int = when (this) {
    NFC_NOT_ENABLED -> R.string.nfc_scanner_error_not_enabled
    NFC_NOT_SUPPORTED -> R.string.nfc_scanner_error_not_supported
    CONNECTION_LOST -> R.string.nfc_scanner_step_connecting_to_document_error
    AUTHENTICATION_FAILED -> R.string.nfc_scanner_step_authenticating_error
    UNKNOWN -> R.string.nfc_scanner_error_unknown
}