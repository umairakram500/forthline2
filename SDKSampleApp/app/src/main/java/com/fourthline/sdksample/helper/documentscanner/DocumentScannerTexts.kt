package com.fourthline.sdksample.helper.documentscanner

import androidx.annotation.StringRes
import com.fourthline.core.DocumentFileSide
import com.fourthline.sdksample.R
import com.fourthline.vision.document.DocumentScannerStep
import com.fourthline.vision.document.DocumentScannerStepResult

@StringRes
fun DocumentScannerStep.getInstructionLabel(): Int = when (fileSide) {
    DocumentFileSide.FRONT ->
        if (isAngled) R.string.document_scanner_step_scan_title_front_angled
        else R.string.document_scanner_step_scan_title_front
    DocumentFileSide.BACK ->
        if (isAngled) R.string.document_scanner_step_scan_title_back_angled
        else R.string.document_scanner_step_scan_title_back
    DocumentFileSide.INSIDE_LEFT ->
        if (isAngled) R.string.document_scanner_step_scan_title_inside_left_angled
        else R.string.document_scanner_step_scan_title_inside_left
    DocumentFileSide.INSIDE_RIGHT ->
        if (isAngled) R.string.document_scanner_step_scan_title_inside_right_angled
        else R.string.document_scanner_step_scan_title_inside_right
}

@StringRes
fun DocumentScannerStep.getSuccessLabel(): Int = when (fileSide) {
    DocumentFileSide.FRONT ->
        if (isAngled) R.string.document_scanner_step_success_front_angled
        else R.string.document_scanner_step_success_front
    DocumentFileSide.BACK ->
        if (isAngled) R.string.document_scanner_step_success_back_angled
        else R.string.document_scanner_step_success_back
    DocumentFileSide.INSIDE_LEFT ->
        if (isAngled) R.string.document_scanner_step_success_inside_left_angled
        else R.string.document_scanner_step_success_inside_left
    DocumentFileSide.INSIDE_RIGHT ->
        if (isAngled) R.string.document_scanner_step_success_inside_right_angled
        else R.string.document_scanner_step_success_inside_right
}

@StringRes
fun DocumentScannerStep.getTitleLabel(): Int = when (isAutoDetectAvailable) {
    true -> R.string.document_scanner_status_automatic_mode_explanation
    false -> R.string.document_scanner_status_manual_mode_explanation
}


@StringRes
fun DocumentScannerStepResult.getConfirmationLabel(): Int =
    R.string.document_scanner_step_result_default