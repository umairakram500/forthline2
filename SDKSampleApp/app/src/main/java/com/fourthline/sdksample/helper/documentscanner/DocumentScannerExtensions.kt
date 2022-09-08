package com.fourthline.sdksample.helper.documentscanner

import com.fourthline.sdksample.R
import com.fourthline.vision.document.DocumentScannerError
import com.fourthline.vision.document.DocumentScannerError.*
import com.fourthline.vision.document.DocumentScannerStep
import com.fourthline.vision.document.DocumentScannerStepWarning
import com.fourthline.vision.document.DocumentScannerStepWarning.*

fun DocumentScannerStep.prettify() =
    "Step: fileSide = $fileSide, isAngled = $isAngled, isAutoDetectAvailable = $isAutoDetectAvailable"

fun List<DocumentScannerStepWarning>.prettify() = joinToString(", ", "[ ", " ]")

/**
 * DocumentScannerStepWarning is currently implemented according to priority.
 * It therefore matches with the ordinal values.
 */
fun List<DocumentScannerStepWarning>.sortByPriority() = sortedBy { it.ordinal }

/**
 * Returns label for the warning with the highest priority.
 */
fun DocumentScannerStepWarning.getWarningLabel() = when (this) {
    DOCUMENT_TOO_DARK -> R.string.document_scanner_step_warning_too_dark
    DEVICE_NOT_STEADY -> R.string.document_scanner_step_warning_not_steady
    GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> R.string.document_scanner_error_play_services
    RECOGNITION_MODELS_NOT_DOWNLOADED -> R.string.document_scanner_step_warning_recognition_models_not_downloaded
}

fun DocumentScannerError.getErrorLabel() = when (this) {
    CAMERA_PERMISSION_NOT_GRANTED -> R.string.document_scanner_error_camera_permission_not_granted
    RECORDING_FAILED -> R.string.document_scanner_error_recording_failed
    SCANNER_INTERRUPTED -> R.string.document_scanner_error_scanner_interrupted
    CAMERA_NOT_AVAILABLE -> R.string.document_scanner_error_camera_permission_not_granted
    RECORD_AUDIO_PERMISSION_NOT_GRANTED -> R.string.document_scanner_error_audio_permission_not_granted
    TIMEOUT -> R.string.document_scanner_error_timeout
    UNKNOWN -> R.string.document_scanner_error_unknown
}