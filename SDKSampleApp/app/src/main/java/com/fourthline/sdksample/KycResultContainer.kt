package com.fourthline.sdksample

import android.graphics.Bitmap
import com.fourthline.core.Gender
import com.fourthline.core.mrz.IdlMrzInfo
import com.fourthline.core.mrz.MrtdMrzInfo
import com.fourthline.core.mrz.MrzInfo
import com.fourthline.kyc.Attachment
import com.fourthline.kyc.Document
import com.fourthline.kyc.KycInfo
import com.fourthline.kyc.Provider
import com.fourthline.nfc.NfcData
import com.fourthline.nfc.NfcDataGroup
import com.fourthline.nfc.NfcScannerResult
import com.fourthline.vision.document.DocumentScannerResult
import com.fourthline.vision.selfie.SelfieScannerResult
import java.util.*

object KycResultContainer {
    var kycInfo: KycInfo = createKycInfo()

    var selfieResult: SelfieScannerResult? = null
        set(value) {
            field = value?.also {
                kycInfo.attachSelfieData(it)
            }
        }

    var documentResult: DocumentScannerResult? = null
        set(value) {
            field = value?.also {
                kycInfo.attachDocumentData(it)
            }
        }

    var nfcResult: NfcScannerResult? = null
        set(value) {
            field = value?.also {
                kycInfo.attachNfcData(it)
            }
        }

    /**
     * Create the KycInfo object to store results of the different parts of the Kyc Process.
     *
     * Apart from the end-user information that is collected, it is required to configure a
     * Provider object to identify your organisation / product.
     */
    private fun createKycInfo() = KycInfo().apply {
        provider = Provider(
            name = "{YOUR_PROVIDER_NAME}",
            clientNumber = "{YOUR_CLIENT_NUMBER}"
        )

        attachPersonAndContactData()
    }

    /**
     * Add Person and Contact data to the KycInfo object.
     *
     * This data is commonly gathered in parts of your user/account registration experience.
     */
    private fun KycInfo.attachPersonAndContactData() {
        person = person.copy(
            firstName = "{FIRST_NAME_OF_END_USER}",
            lastName = "{LAST_NAME_OF_END_USER}",
            nationalityCode = "NLD",
            gender = Gender.UNKNOWN,
            birthDate = Calendar.getInstance().apply { set(1900, 1, 1) }.time
        )

        contacts = contacts.copy(
            email = "name@host.com",
            mobile = "+31123456789",
            //phone = "+31123456789", // Optional
        )
    }

    /**
     * Add Selfie Result data to the KycInfo object.
     */
    private fun KycInfo.attachSelfieData(selfieScannerResult: SelfieScannerResult) {
        selfie = with(selfieScannerResult) {
            Attachment.Selfie(
                image = image.full,
                timestamp = metadata.timestamp.time,
                location = metadata.location,
                videoRecording = videoRecording
            )
        }
    }

    /**
     * Add Document data to the KycInfo object.
     */
    private fun KycInfo.attachDocumentData(documentScannerResult: DocumentScannerResult) {
        document = with(documentScannerResult) {
            Document().copy(
                type = documentType,
                number = getDocumentNumber(),
                videoRecording = videoRecording,
                images = stepResults.map {
                    Attachment.Document(
                        it.image.full,
                        it.metadata.fileSide,
                        it.metadata.isAngled,
                        it.metadata.timestamp.time,
                        it.metadata.location
                    )
                }
            )
        }
    }

    /**
     * Add Nfc Result data to the KycInfo object.
     */
    private fun KycInfo.attachNfcData(nfcScannerResult: NfcScannerResult) {
        document?.nfc = nfcScannerResult.toNfcAttachment()
    }
}

private fun DocumentScannerResult.getDocumentNumber() : String {
    val result = when (val data = mrzInfo) {
        is MrtdMrzInfo -> {
            data.documentNumber
        }
        is IdlMrzInfo -> {
            data.documentNumber
        }
        else -> ""
    }

    return result
}

private fun NfcScannerResult.toNfcAttachment() = Attachment.Nfc(
    image = getData<Bitmap>(NfcData.NfcDataType.PHOTO),
    mrz = getData<MrzInfo>(NfcData.NfcDataType.MRZ_INFO)?.rawMrz,
    dataGroups = getDataGroups()
        .associateBy (NfcDataGroup::groupNumber, NfcDataGroup::rawData)
)