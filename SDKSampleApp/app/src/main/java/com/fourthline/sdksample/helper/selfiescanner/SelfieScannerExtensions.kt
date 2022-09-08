package com.fourthline.sdksample.helper.selfiescanner

import androidx.annotation.StringRes
import com.fourthline.sdksample.R
import com.fourthline.vision.selfie.SelfieScannerError
import com.fourthline.vision.selfie.SelfieScannerError.*
import com.fourthline.vision.selfie.SelfieScannerStep
import com.fourthline.vision.selfie.SelfieScannerStep.*
import com.fourthline.vision.selfie.SelfieScannerWarning
import com.fourthline.vision.selfie.SelfieScannerWarning.*

@StringRes
fun SelfieScannerStep.getInstructionLabel() = when (this) {
    SELFIE -> R.string.selfie_scanner_step_selfie
    TURN_HEAD_LEFT -> R.string.selfie_scanner_step_turn_head_left
    TURN_HEAD_RIGHT -> R.string.selfie_scanner_step_turn_head_right
}

/**
 * SelfieScannerWarning is currently implemented according to priority.
 * It therefore matches with the ordinal values.
 */
fun List<SelfieScannerWarning>.sortByPriority() = sortedBy { it.ordinal }

@StringRes
fun SelfieScannerWarning.getWarningLabel() =
    when (this) {
        FACE_NOT_DETECTED -> R.string.selfie_scanner_warning_face_not_detected
        FACE_NOT_IN_FRAME -> R.string.selfie_scanner_warning_face_not_in_frame
        FACE_TOO_CLOSE -> R.string.selfie_scanner_warning_face_too_close
        FACE_TOO_FAR -> R.string.selfie_scanner_warning_face_too_far
        FACE_YAW_TOO_BIG -> R.string.selfie_scanner_warning_yaw_too_big
        EYES_CLOSED -> R.string.selfie_scanner_warning_eyes_closed
        DEVICE_NOT_STEADY -> R.string.selfie_scanner_warning_device_not_steady
    }

@StringRes
fun SelfieScannerError.getErrorLabel() = when (this) {
    SelfieScannerError.MULTIPLE_FACES_DETECTED -> R.string.selfie_scanner_error_multiple_faces_detected
    TIMEOUT -> R.string.selfie_scanner_error_timeout
    FACE_DISAPPEARED -> R.string.selfie_scanner_error_face_disappeared
    CAMERA_PERMISSION_NOT_GRANTED -> R.string.selfie_scanner_error_camera_permission_not_granted
    RECORDING_FAILED -> R.string.selfie_scanner_error_recording_failed
    SCANNER_INTERRUPTED -> R.string.selfie_scanner_error_scanner_interrupted
    GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> R.string.selfie_scanner_error_play_services
    RECOGNITION_MODELS_NOT_DOWNLOADED -> R.string.selfie_scanner_error_models_not_downloaded
    UNKNOWN,
    CAMERA_NOT_AVAILABLE,
    RECORD_AUDIO_PERMISSION_NOT_GRANTED -> R.string.selfie_scanner_error_unknown
}