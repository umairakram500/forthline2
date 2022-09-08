package com.fourthline.sdksample.helper.documentscanner

import androidx.annotation.DrawableRes
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.sdksample.R
import com.fourthline.vision.document.DocumentScannerStep

@DrawableRes
fun DocumentScannerStep.getDocumentMask(
    documentType: DocumentType,
    maskMode: MaskMode = MaskMode.MASK_NORMAL
) =
    when (maskMode) {
        MaskMode.MASK_NORMAL -> getDocumentMaskNormal(documentType)
        MaskMode.MASK_ORANGE -> getDocumentMaskOrange(documentType)
        MaskMode.MASK_GREEN -> getDocumentMaskGreen(documentType)
        MaskMode.MASK_RESULT -> getDocumentMaskResult(documentType)
    }

private fun DocumentScannerStep.getDocumentMaskNormal(documentType: DocumentType): Int {
    return when (documentType) {
        DocumentType.PASSPORT ->
            if (isAngled) R.drawable.document_mask_passport_front_tilted
            else R.drawable.document_mask_passport_front
        DocumentType.ID_CARD, DocumentType.RESIDENCE_PERMIT -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_id_card_front_tilted
                else R.drawable.document_mask_id_card_front
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_card_back_tilted
                else R.drawable.document_mask_id_card_back
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.FRENCH_ID_CARD -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_french_id_front_tilted
                else R.drawable.document_mask_french_id_front
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_french_id_back_tilted
                else R.drawable.document_mask_french_id_back
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_driver_license_front_tilted
                else R.drawable.document_mask_driver_license_front
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted
                else R.drawable.document_mask_driver_license_back
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PAPER_ID -> when (fileSide) {
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_paper_back_tilted
                else R.drawable.document_mask_id_paper_back
            DocumentFileSide.INSIDE_LEFT ->
                if (isAngled) R.drawable.document_mask_id_paper_left_tilted
                else R.drawable.document_mask_id_paper_left
            DocumentFileSide.INSIDE_RIGHT ->
                if (isAngled) R.drawable.document_mask_id_paper_right_tilted
                else R.drawable.document_mask_id_paper_right
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DUTCH_DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (
                    isAngled) R.drawable.document_mask_dutch_driver_license_front_tilted
                else R.drawable.document_mask_dutch_driver_license_front
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted
                else R.drawable.document_mask_driver_license_back
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PROOF_OF_ADDRESS -> {
            return -1
        }
    }
}

private fun DocumentScannerStep.getDocumentMaskOrange(documentType: DocumentType): Int {
    return when (documentType) {
        DocumentType.PASSPORT ->
            if (isAngled) R.drawable.document_mask_passport_front_tilted_orange
            else R.drawable.document_mask_passport_front_orange
        DocumentType.ID_CARD, DocumentType.RESIDENCE_PERMIT -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_id_card_front_tilted_orange
                else R.drawable.document_mask_id_card_front_orange
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_card_back_tilted_orange
                else R.drawable.document_mask_id_card_back_orange
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.FRENCH_ID_CARD -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_french_id_front_tilted_orange
                else R.drawable.document_mask_french_id_front_orange
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_french_id_back_tilted_orange
                else R.drawable.document_mask_french_id_back_orange
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_driver_license_front_tilted_orange
                else R.drawable.document_mask_driver_license_front_orange
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_orange
                else R.drawable.document_mask_driver_license_back_orange
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PAPER_ID -> when (fileSide) {
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_paper_back_tilted_orange
                else R.drawable.document_mask_id_paper_back_orange
            DocumentFileSide.INSIDE_LEFT ->
                if (isAngled) R.drawable.document_mask_id_paper_left_tilted_orange
                else R.drawable.document_mask_id_paper_left_orange
            DocumentFileSide.INSIDE_RIGHT ->
                if (isAngled) R.drawable.document_mask_id_paper_right_tilted_orange
                else R.drawable.document_mask_id_paper_right_orange
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DUTCH_DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_dutch_driver_license_front_tilted_orange
                else R.drawable.document_mask_dutch_driver_license_front_orange
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_orange
                else R.drawable.document_mask_driver_license_back_orange
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PROOF_OF_ADDRESS -> {
            return -1
        }
    }
}

private fun DocumentScannerStep.getDocumentMaskGreen(documentType: DocumentType): Int {
    return when (documentType) {
        DocumentType.PASSPORT ->
            if (isAngled) R.drawable.document_mask_passport_front_tilted_green
            else R.drawable.document_mask_passport_front_green

        DocumentType.ID_CARD, DocumentType.RESIDENCE_PERMIT -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_id_card_front_tilted_green
                else R.drawable.document_mask_id_card_front_green
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_card_back_tilted_green
                else R.drawable.document_mask_id_card_back_green
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.FRENCH_ID_CARD -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_french_id_front_tilted_green
                else R.drawable.document_mask_french_id_front_green
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_french_id_back_tilted_green
                else R.drawable.document_mask_french_id_back_green
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_driver_license_front_tilted_green
                else R.drawable.document_mask_driver_license_front_green
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_green
                else R.drawable.document_mask_driver_license_back_green
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PAPER_ID -> when (fileSide) {
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_paper_back_tilted_green
                else R.drawable.document_mask_id_paper_back_green
            DocumentFileSide.INSIDE_LEFT ->
                if (isAngled) R.drawable.document_mask_id_paper_left_tilted_green
                else R.drawable.document_mask_id_paper_left_green
            DocumentFileSide.INSIDE_RIGHT ->
                if (isAngled) R.drawable.document_mask_id_paper_right_tilted_green
                else R.drawable.document_mask_id_paper_right_green
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DUTCH_DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_dutch_driver_license_front_tilted_green
                else R.drawable.document_mask_dutch_driver_license_front_green
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_green
                else R.drawable.document_mask_driver_license_back_green
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PROOF_OF_ADDRESS -> {
            return -1
        }
    }
}

private fun DocumentScannerStep.getDocumentMaskResult(documentType: DocumentType): Int {
    return when (documentType) {
        DocumentType.PASSPORT ->
            if (isAngled) R.drawable.document_mask_passport_front_tilted_result
            else R.drawable.document_mask_passport_front_result
        DocumentType.ID_CARD, DocumentType.RESIDENCE_PERMIT -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_id_card_front_tilted_result
                else R.drawable.document_mask_id_card_front_result
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_card_back_tilted_result
                else R.drawable.document_mask_id_card_back_result
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.FRENCH_ID_CARD -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_french_id_front_tilted_result
                else R.drawable.document_mask_french_id_front_result
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_french_id_back_tilted_result
                else R.drawable.document_mask_french_id_back_result
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_driver_license_front_tilted_result
                else R.drawable.document_mask_driver_license_front_result
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_result
                else R.drawable.document_mask_driver_license_back_result
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.DUTCH_DRIVERS_LICENSE -> when (fileSide) {
            DocumentFileSide.FRONT ->
                if (isAngled) R.drawable.document_mask_dutch_driver_license_front_tilted_result
                else R.drawable.document_mask_dutch_driver_license_front_result
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_driver_license_back_tilted_result
                else R.drawable.document_mask_driver_license_back_result
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PAPER_ID -> when (fileSide) {
            DocumentFileSide.BACK ->
                if (isAngled) R.drawable.document_mask_id_paper_back_tilted_result
                else R.drawable.document_mask_id_paper_back_result
            DocumentFileSide.INSIDE_LEFT ->
                if (isAngled) R.drawable.document_mask_id_paper_left_tilted_result
                else R.drawable.document_mask_id_paper_left_result
            DocumentFileSide.INSIDE_RIGHT ->
                if (isAngled) R.drawable.document_mask_id_paper_right_tilted_result
                else R.drawable.document_mask_id_paper_right_result
            else -> throw RuntimeException("Document side: $fileSide is not present in document: $documentType")
        }
        DocumentType.PROOF_OF_ADDRESS -> {
            return -1
        }
    }
}

enum class MaskMode {
    MASK_NORMAL,
    MASK_ORANGE,
    MASK_GREEN,
    MASK_RESULT
}

private const val FRACTION_PASSPORT_MASK_ADJUSTMENT = 0.207F