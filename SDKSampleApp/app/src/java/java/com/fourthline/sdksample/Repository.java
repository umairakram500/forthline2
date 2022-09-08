/*
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample;

import static com.fourthline.core.DocumentType.PASSPORT;
import static com.fourthline.sdksample.Utils.asString;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.fourthline.core.DocumentFileSide;
import com.fourthline.core.DocumentType;
import com.fourthline.core.Gender;
import com.fourthline.core.mrz.MrzInfo;
import com.fourthline.kyc.Attachment;
import com.fourthline.kyc.Contacts;
import com.fourthline.kyc.DeviceMetadata;
import com.fourthline.kyc.Document;
import com.fourthline.kyc.KycInfo;
import com.fourthline.kyc.Person;
import com.fourthline.kyc.Provider;
import com.fourthline.kyc.QesAttachment;
import com.fourthline.kyc.SecondaryDocument;
import com.fourthline.kyc.zipper.Zipper;
import com.fourthline.kyc.zipper.ZipperError;
import com.fourthline.nfc.NfcData;
import com.fourthline.nfc.NfcDataGroup;
import com.fourthline.nfc.NfcScannerResult;
import com.fourthline.vision.document.DocumentScannerResult;
import com.fourthline.vision.document.DocumentScannerStepMetadata;
import com.fourthline.vision.document.DocumentScannerStepResult;
import com.fourthline.vision.selfie.SelfieScannerResult;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kotlin.Pair;

public class Repository {
    public KycInfo kyc = new KycInfo();

    // Don't do this, don't leak context
    private Context context;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public Repository(Context context) {
        this.context = context;

        Bitmap bitmap = Utils.createBitmap();
        Pair<Double, Double> location = new Pair<>(52.3622, 4.8793);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1965, 2, 10);
        Date birthDate = calendar.getTime();
        calendar.set(2014, 2, 10);
        Date issueDate = calendar.getTime();
        calendar.set(2024, 2, 10);
        Date expirationDate = calendar.getTime();

        Map<Integer, byte[]> nfcDataGroups = new HashMap<>();
        nfcDataGroups.put(1, new byte[0]);

// Provider
        Provider provider = kyc.getProvider();
        provider.setName("Your Provider");
        provider.setClientNumber("ClientNumber");

// Person
        Person person = kyc.getPerson();
        person.setFirstName("WILLEKE LISELOTTE");
        person.setLastName("DE BRUIJN");
        person.setGender(Gender.FEMALE);
        person.setNationalityCode("NLD");
        person.setBirthDate(birthDate);

// Person's optional fields
//        person.setMiddleName("MIDDLENAME");
//        person.setBirthCountryCode("NLD");
//        person.setBirthPlace("Amsterdam");
//

// Device Metadata
// Ip address is optional, location is required and should not be null
        DeviceMetadata metadata = new DeviceMetadata("10.10.10.10", location);
        kyc.setMetadata(metadata);

// Address (optional)
//        Address address = new Address();
//        address.setStreet("Tesselschadestraat");
//        address.setStreetNumber(12);
//        address.setStreetNumberSuffix("A");
//        address.setPostalCode("1054 ET");
//        address.setCity("Amsterdam");
//        address.setRegion("North Holland");
//        address.setCountryCode("NLD");
//
//        kyc.setAddress(address);

// Contacts
        Contacts contacts = kyc.getContacts();
        contacts.setEmail("info@fourthline.com");
        contacts.setMobile("+31123456789");
// Optional fields
//        contacts.setPhone("+31123456789");

// Selfie Attachment (optional)
        Attachment.Selfie selfie = new Attachment.Selfie(bitmap);
// Optional fields
//        selfie.setTimestamp(new Date().getTime());
//        selfie.setLocation(location);
//        selfie.setVideoUrl(createTempFile());

        kyc.setSelfie(selfie);

// Document Attachment (auxiliary data)
        Attachment.Document documentFrontPage = new Attachment.Document(
                bitmap,
                DocumentFileSide.FRONT,
                false
        );
// Optional fields
//        documentFrontPage.setTimestamp(new Date().getTime());
//        documentFrontPage.setLocation(location);

        List<Attachment.Document> documentPages = new ArrayList<>();
        documentPages.add(documentFrontPage);

// Document (optional)
        Document document = new Document(PASSPORT);
        document.setImages(documentPages);
        document.setNumber("SPECI2014");
// Optional fields
//        document.setIssueDate(issueDate);
//        document.setExpirationDate(expirationDate);
//        document.setVideoUrl(createTempFile());


// NFC Attachment (auxiliary data)
        Attachment.Nfc nfcAttachment = new Attachment.Nfc(
                bitmap,
                "P<NLDDE<BRUIJN<<WILLEKE<LISELOTTE<<<<<<<<<<<\n" +
                        "SPECI20142NLD6503101F2403096999999990<<<<<84"
// Optional fields
//                new Date().getTime(),
//                location,
//                nfcDataGroups
        );

        document.setNfc(nfcAttachment);

        kyc.setDocument(document);

// Secondary Document (optional)
//        SecondaryDocument secondaryDocument = new SecondaryDocument(DocumentType.ID_CARD);
//
//        List<Attachment.Document> secondaryDocumentPages = new ArrayList<>();
//        Attachment.Document secondaryDocumentFrontPage = new Attachment.Document(
//                bitmap,
//                DocumentFileSide.FRONT,
//                false
//        );
//        secondaryDocumentPages.add(secondaryDocumentFrontPage);
//
//        secondaryDocument.setImages(secondaryDocumentPages);
// Optional fields
//        secondaryDocument.setIssueDate(issueDate);
//        secondaryDocument.setExpirationDate(expirationDate);
//
//        List<SecondaryDocument> secondaryDocuments = new ArrayList<>();
//        secondaryDocuments.add(secondaryDocument);
//
//        kyc.setSecondaryDocuments(secondaryDocuments);

// Profession (optional)
// All fields are optional
//        Profession profession = new Profession(
//                EmploymentStatus.EMPLOYED,
//                "Engineer",
//                Industry.INFORMATION_COMMUNICATION
//        );
//
//        kyc.setProfession(profession);

// QesAttachment (optional for normal KYC flow and mandatory for QES flow)
//        QesAttachment documentToSign = new QesAttachment(
//                UUID.randomUUID(),
//                Utils.createPdfFile(context)
//        );
//        List<QesAttachment> documentsToSign = new ArrayList<>();
//        documentsToSign.add(documentToSign);
//
//        kyc.setDocumentsToSign(documentsToSign);
    }

    @NotNull
    public String createZip() {
        String result;
        try {
            URI zipUri = new Zipper().createZipFile(kyc, context);

            result = "KYC ZIP URL: " + zipUri.toString();
        } catch (ZipperError error) {
            Log.e(Repository.class.getName(), "Zipper failed with error: " + error);

            result = "Error: " + asString(error);
        }

        Utils.vibrate(context);

        return result;
    }

    //region attaching scanner results to KYC
    private void attach(SelfieScannerResult selfieResult) {
        Attachment.Selfie selfie = new Attachment.Selfie(selfieResult.getImage().getFull());
        selfie.setTimestamp(selfieResult.getMetadata().getTimestamp().getTime());
        selfie.setLocation(selfieResult.getMetadata().getLocation());
        selfie.setVideoUrl(selfieResult.getVideoUrl());

        kyc.setSelfie(selfie);
    }

    private void attach(DocumentScannerResult documentResult, List<DocumentScannerStepResult> stepResults) {
        List<Attachment.Document> documentPages = new ArrayList<>();

        for (DocumentScannerStepResult stepResult : stepResults) {
            DocumentScannerStepMetadata metadata = stepResult.getMetadata();
            Attachment.Document documentPage = new Attachment.Document(
                    stepResult.getImage().getFull(),
                    metadata.getFileSide(),
                    metadata.isAngled(),
                    metadata.getTimestamp().getTime(),
                    metadata.getLocation()
            );

            documentPages.add(documentPage);
        }

        // please note that type, number, issueDate and expirationDate should be set separately as part of user input
        Document document = new Document();

        document.setVideoUrl(documentResult.getVideoUrl());
        document.setImages(documentPages);

        kyc.setDocument(document);
    }

    private void attach(NfcScannerResult nfcResult) {
        Map<Integer, byte[]> dataGroups = new HashMap<>();
        for (NfcDataGroup group : nfcResult.getDataGroups()) {
            dataGroups.put(group.getGroupNumber(), group.getRawData());
        }

        MrzInfo mrz = nfcResult.getData(NfcData.NfcDataType.MRZ_INFO);
        String rawMrz = null;
        if (mrz != null) rawMrz = mrz.getRawMrz();

        Attachment.Nfc nfcAttachment = new Attachment.Nfc(
                nfcResult.getData(NfcData.NfcDataType.PHOTO),
                rawMrz,
                nfcResult.getMetadata().getTimestamp().getTime(),
                nfcResult.getMetadata().getLocation(),
                dataGroups
        );
    }
    //endregion

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private URI createTempFile() {
        return Utils.createEmptyTempFile(context);
    }
}