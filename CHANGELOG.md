# Android Fourthline SDK

# Changelog

## [2.14.0] - 07-09-2022
### Added
- `:fourthline-sdk` - Support for Document Validation feature of Standalone Document Scanner.
- `:fourthline-sdk` - Polish and Romanian translations.

### Changed
- Updated dependencies to the latest versions for:
    - Kotlin version to 1.7.10.
    - Kotlin Coroutines version to 1.6.4.
    - Compose dependencies versions to 1.2.1.
    - Compose compiler version to 1.3.0.
    - Compose accompanist dependencies versions to 0.25.1.
    - Compose Activity version to 1.5.1.
    - Compose Navigation version to 2.5.1.
    - Dagger version to 2.43.2.
    - `compileSdkVersion` to 32.
- `:fourthline-sdk` - Updated icons on various screens.
- `:fourthline-sdk` - 'Try again' button on Error Popup for Document Scanner now dismisses the scanner. These change do no apply for the Standalone Document Scanner.
- `:fourthline-sdk` - Updated scaling of image during confirmation of scanners to always fit the content.

### Removed
- `:fourthline-sdk` - 'Cancel' button on Error Popup for Document Scanner. These change do no apply for the Standalone Document Scanner.

## [2.13.2] - 23-08-2022
### Added
- `:fourthline-sdk` - Tracking crashes using Datadog when:
    - analytics is enabled by client;
    - any of stacktrace element starts with "com.fourthline", to prevent tracking client's crashes.
- `:fourthline-sdk` - Dependency on `com.google.accompanist:accompanist-systemuicontroller` to help fixing issues.

### Fixed
- `:fourthline-vision` - Users are not able to use automatic scanners due to issues with accelerometer on Realme 8 devices.
- `:fourthline-sdk` - Issues with the system bars:
    - force system bars to use the `OrcaColors.screen.backgroundColor` instead of default black/transparent;
    - prevent showing action bar from the outside app in some rare cases.

## [2.13.1] - 15-08-2022
### Added
- `:fourthline-sdk` - Icons for guidance to Selfie Intro screen.

## [2.13.0] - 11-08-2022
### Added
- `:fourthline-vision` - Cropping of image returned as `ScannerImage.cropped` of the document scanner - cropping is based on the actual position of scanned document.
- `:fourthline-sdk` - The color of graphics in the info popups can now be customised by setting a value for the `OrcaGraphic.primaryColor`.
- `:fourthline-sdk` - Allow providing fonts from file.

### Changed
- `:fourthline-core` - First and last names in `MrzInfo` are returned in all upper case to be aligned with iOS implementation.
- `:fourthline-vision` - Trimmed selfie video to start one second before the first liveness check step.
- `:fourthline-sdk` - `OrcaFonts.Font` class was replaced with sealed version to allow providing fonts from the file. To migrate replace `OrcaFonts.Font(..)` with `OrcaFonts.Font.FromFontRes(..)`.

### Fixed
- `:fourthline-sdk` - Provided `KycInfo` object is handled properly.

## [2.12.1] - 25-07-2022
### Changed
- `:fourthline-sdk` - Prevent Screen Sleep mode during Scanning.

## [2.12.0] - 13-07-2022
### Added
- `:fourthline-core` - Extended `IdlMrzInfo` class with readonly field: `documentNumber`.
- `:fourthline-sdk` - Pre-fills `documentNumber` field on Document Details page for International Drivers Licences.

### Fixed
- `:fourthline-kyc` - Edge-case that caused `Zipper` to fail when setting region information.
- `:fourthline-sdk` - Prevent unexpected behaviour when double-tapping on certain interactive elements.

### Removed
- `:fourthline-sdk` - Removed `ContactInfo` feature.

## [2.11.2] - 30-06-2022
### Added
- `:fourthline-sdk` - `OrcaError` is updated with `ISSUING_COUNTRY_NOT_SUPPORTED`, `DOCUMENT_EXPIRED` and `DOCUMENT_TYPE_NOT_SUPPORTED` as reasons why a user could not finish the flow.

### Changed
- `:fourthline-sdk` - Updated UI and handling of blocking errors in response to Document validation.
- `:fourthline-sdk` - Deprecated `ContactInfo` functionality on Orca.Builder.

### Fixed
- `:fourthline-vision` - Edge-case that caused `DocumentType.ID_CARD` to be recognised as a `DocumentType.DUTCH_DRIVERS_LICENSE`.

## [2.11.1] - 21-06-2022
### Added
- `:fourthline-vision` - `stepsCount` field to `DocumentScanner`, representing the total number of document scanner steps.
- `:fourthline-vision` - `index` field to `DocumentScannerStep`, representing the step's position in the total number of document scanner steps, 0-based.
- `:fourthline-vision` - `onStepsCountUpdate` function with default implementation to `DocumentScannerCallback`.
- `:fourthline-sdk` - New popup in the document scanner before taking tilted document images.

### Changed
- `:fourthline-sdk` - DocumentScanner title position closer to the document mask.

### Fixed
- `:fourthline-sdk` - Issue with `buttons.scannerPrimary.backgroundColor` field of `OrcaColors` not being mapped correctly to the UI.
- `:fourthline-sdk` - Issue that caused a crash when starting and closing Orca multiple times.
- `:fourthline-sdk` - Issue that caused a popup to show at an incorrect moment in Selfie Scanner.

## [2.11.0] - 08-06-2022
### Added
- `:fourthline-sdk` - German translations.

### Changed
- `:fourthline-sdk` - `OrcaColors` updated for more granular styling of UI elements. Please consult documentation for more information.

## [2.10.0] - 23-05-2022
### Added
- `:fourthline-vision` - `DocumentScannerStepError.INVALID_CONTENT_DETECTION_AREA` to prevent crashes when scanner is misconfigured and returns an error instead.
- `:fourthline-sdk` - Support for Document Validation feature of Document Scanner.
- `:fourthline-sdk` - `ContactInfo` to enable customisation for Document Validation feature.
- `:fourthline-kyc` - Extended `DeviceMetadata` class with readonly fields: `language`, `region`, `model`, `osVersion`, `sdkVersion`, `analyticsId`, `osCompromised`.

### Changed
- Kotlin Coroutines version to 1.6.1.
- `:fourthline-sdk` - Error popups style.
- `:fourthline-sdk` - Improved UI of NFC results for Spanish ID cards.
- `:fourthline-sdk` - Improved localisation strings.

### Fixed
- `:fourthline-nfc` - Timezone set to UTC for the returned NFC Scanner results in case of scanning Drivers Licenses.
- `:fourthline-sdk` - UI and navigation bugs.
- `:fourthline-sdk` - Incorrectly parsed colors when using `OrcaColor.FromInt()`.
- `:fourthline-sdk` - Possible crash when user changes country and tries to continue with unsupported document type for this country.
- `:fourthline-sdk` - Disable generating PNG files at build time which resulted in reduced size of the apk.

## [2.9.2] - 13-04-2022
### Changed
- Updated compose libraries to the latest stable versions.

## [2.9.1] - 06-04-2022
### Added
- Support for new Romanian ID cards (first issued on 02/08/2021) in `:fourthline-sdk` module.
  The user is presented with the option of choosing their Romanian ID card type.
- Support for Analytics in `:fourthline-sdk` module.
- `layouts` for `OrcaFlavor` which represents the layout dimensions to be used in `:fourthline-sdk` module.
- `primaryButtonCornerRadius` for `OrcaLayout` for primary button customization in `:fourthline-sdk` module.
- `OrcaLayouts.CornerRadius` in `:fourthline-sdk` module.

### Fixed
- UI bugs in `:fourthline-sdk` module.

## [2.9.0] - 24-03-2022
### Added
- `VideoRecording` and `VideoDuration` classes to `:fourthline-core` module.
- Video location support for `Attachment.Selfie` and `Document` in `:fourthline-kyc` module.
- `INVALID_VIDEO_LENGTH` and `INVALID_VIDEO_LOCATION` validation errors for `Attachment.Selfie` in `:fourthline-kyc` module.
- `INVALID_VIDEO_LENGTH` and `INVALID_VIDEO_LOCATION` validation errors for `Document` in `:fourthline-kyc` module.
- `TaxInfo` - support for tax information for `KycInfo` in `:fourthline-kyc` module.
- `videoDuration` and `includeAngledSteps` options for `OrcaDocumentFlowConfig` in `:fourthline-sdk` module.
- `videoDuration` option for `DocumentScannerConfig` in `:fourthline-vision` module.
- Support for multiple `DocumentVersion` (French ID).
- Support for UI with different languages (English, Dutch, French, Italian, Spanish) in `:fourthline-sdk` module.
- Support for `popupHeader` and `popupMessage` in `OrcaColors` in `:fourthline-sdk` module.

### Changed
Breaking changes
- Replaced `SelfieScannerResult.videoUrl` with `videoRecording` of type `VideoRecording` in `:fourthline-vision` module.
- Replaced `DocumentScannerResult.videoUrl` with `videoRecording` of type `VideoRecording` in `:fourthline-vision` module.
- Replaced `Attachment.Selfie.videoUrl` with `videoRecording` of type `VideoRecording` in `:fourthline-kyc` module.
- Replaced `Document.videoUrl` with `videoRecording` of type `VideoRecording` in `:fourthline-kyc` module.

## [2.8.2] - 09-03-2022
### Added
- Haptic feedback `:fourthline-sdk` module.
- Option to provide custom colors to `OrcaColors` using `android.graphics.Color`.
- More customisation options for `:fourthline-sdk` module. For more info see `OrcaLayout`.

### Changed
- `context` parameter is moved from `present` function to `Orca.Builder`.

### Fixed
- UI bugs and inconsistencies in `:fourthline-sdk`.

## [2.8.1] - 23-02-2022
### Added
- `DatadogAlreadyInitialized` case to `AnalyticsError`.
- Support for customization of colors and fonts in `:fourthline-sdk` module.

## [2.8.0] - 09-02-2022
### Added
- `LocationProvider`, a wrapper around `FusedLocationProviderClient` which simplifies interaction with location services.
- `Context.locationServicesEnabled` extension helper. Java version: `FourthlineLocationServicesHelper.getLocationServicesEnabled(context)`

### Changed
- `KycInfo.metadata` field from optional to required.
- `DeviceMetadata.location` field from optional to required.

### Fixed
- Frozen preview issue in Selfie and Document scanner on some specific devices (Xiaomi Redmi 9T).
- Compose dependency clash in `:fourthline-sdk` module.

## [2.7.0] - 27-01-2022
### Added
- Helper for deleting all files created by Fourthline SDK. Kotlin: `Context.deleteFourthlineFiles()`, Java: `FourthlineFileManager.deleteFourthlineFiles(Context context)`.
- NFC flow to :fourthline-sdk module.
- Contact details flow to :fourthline-sdk module.
- Address details flow to :fourthline-sdk module.

### Changed
- Kotlin version to 1.6.0.
- Location for storing videos recorded by Selfie and Document Scanners from `APP_CACHE/fourthline_video` to `APP_CACHE/fourthline`.

### Fixed
- Correctly extract language from `Locale` that is used in :fourthline-kyc module.

### Removed
- Functionality that was deprecated in previous releases: `MULTIPLE_FACES_DETECTED` warning, possibility to take snapshot and manual mode from Selfie Scanner. Fourthline REQUIRES liveness check for all clients and it was skipped in manual mode.

## [2.6.0] - 03-01-2022
### Added
- beta version of :fourthline-sdk module, a drop-in solution handling basic flows (Selfie, Document and Location) and UI in order to integrate the full KYC journey in a short period of time.
- `HIGH_SAMPLING_RATE_SENSORS` permission to manifest of :fourthline-vision module to fix crashing on Android 12.

### Changed
- `getOverlayView()` was moved from `DocumentScannerCallback` to `DocumentScanner` interface.
- Rules were relaxed to allow Selfie, Document and NFC scanners to grab location even if only `ACCESS_COARSE_LOCATION` is granted. It is still recommended to request `ACCESS_FINE_LOCATION`.

### Fixed
- Crash on Android 12 due to polling data from sensors too quick.

## [2.5.2] - 07-12-2021
### Changed
- Datadog version from 1.9.1 to 1.10.0

### Fixed
- Bug in Analytics where Android logs were not sent to Datadog.

## [2.5.1] - 17-11-2021
### Changed
- Timeouts in Selfie module were updated to 15 seconds.
- Timeouts in Document module were updated to 40 seconds.
- `LivenessCheckType.NONE` is deprecated and will be removed in the next versions.
- `SelfieScannerStep.MANUAL_SELFIE` is deprecated and will be removed in the next versions.
- `SelfieScannerConfig.includeManualSelfiePolicy` is deprecated and will be removed in the next versions.

## [2.5.0] - 29-10-2021
### Added
- Support for the proof of address flow.
- New `DocumentType` `PROOF_OF_ADDRESS`. Note that this document type can't be used as a primary document in the KYC module.

### Changed
- Renamed `KycInfo.secondaryDocument` to `KycInfo.secondaryDocuments`.
- Changed `KycInfo.secondaryDocument` to be a list of documents (`KycInfo.secondaryDocuments`).

## [2.4.1] - 21-10-2021
### Changed
- Improved OCR corrections for extracting MRZ data.
- Updated version of AGP to 7.0.2.

## [2.4.0] - 23-09-2021
### Added
- Qualified Electronic Signature (QES) support for regular KYC flow. See `KycInfo.documentsToSign`.
- Support for recording videos with audio.
- `DocumentValidationConfig` and `DocumentValidationError` as a part of document validation feature.
- `validationConfig` to `DocumentScannerConfig` as a part of document validation feature.
- Set of `validationErrors` to `DocumentScannerStepResult` as a part of document validation feature.
- `documentType` and `stepResults` fields to `DocumentScannerResult` for convenience.

### Changed
- `MrtdMrzInfo` birth date and expiration date types from `String` to `Date`.
- `SelfieScannerConfig` and `DocumentScannerConfig`  configuration `shouldRecordVideo` has been replaced with `recordingType` enum to support multiple record options:
    - `NONE`
    - `VIDEO_ONLY`
    - `VIDEO_WITH_AUDIO`
  Default case for `recordingType` is `VIDEO_ONLY`.

## [2.3.0] 25-08-2021
### Added
- `FourthlineAnalytics` class to initialise analytics and set consent. Nothing is sent back when analytics is not initialised and/or datadog dependency is not available and/or explicit consent is not given.
- `GOOGLE_PLAY_SERVICES_NOT_AVAILABLE` warning to document scanner.
- `GOOGLE_PLAY_SERVICES_NOT_AVAILABLE` error to selfie scanner. Note: when manual policy is included and the error occurs, the scanner switches to `MANUAL_SELFIE` step.

### Fixed
- Crashes when play services are not installed or available or updated, instead an error (selfie scanner) or warning (document scanner) is returned.
- Profession container is now always added to the XML upon creating zip file.

## [2.2.11] 28-06-2021
### Fixed
- Crashes related to video recording (Android MediaCodec) on some particular devices.
- Validation of nationality, country of birth and address country in KYC module for country code of Germany retrieved from MRZ: `D` is now a valid value for KYC module.

## [2.2.10] 21-06-2021
### Added
- `DocumentScannerStepWarning` at Vision Document Scanner are emitted also for steps with `DocumentScannerStep.isAutoDetectAvailable = false`.
- `MrtdMrzInfo` extended with `optionalData` field.
- `SelfieScannerError.MULTIPLE_FACES_DETECTED` was added to prevent sending cases which are going to be rejected.

### Changed
- Document number parsing for MRTD documents was enhanced: longer document numbers are parsed properly now.
- `SelfieScannerWarning.MULTIPLE_FACES_DETECTED` is deprecated, instead `SelfieScannerError.MULTIPLE_FACES_DETECTED` is returned in such cases.

## [2.2.9] 27-05-2021
### Fixed
- Crash when device camera is not available, instead an error is returned.
- Crash when device has nonconforming implementation of camera focusing. (introduced in 2.2.8)
- Crash when device has nonconforming implementation of camera characteristics. (introduced in 2.2.8)

### Added
- `CAMERA_NOT_AVAILABLE` error to Selfie and Document scanners.

## [2.2.8] - 12-05-2021
### Note: this version contains bugs in the Vision scanners. Please use SDK version 2.2.9 or higher.

### Fixed
- Crash upon recreating view in Vision fragments.

### Changed
- Increased output image resolution for Vision Document scanner.
- Camera focusing area position to match the provided detection area in Vision scanners. Note: some devices (mostly Huawei and Samsung) ignore this information and keep focusing in the center of the overlay view, please, keep the detection area and masks in the center of overlay view.
- Validations in KYC module. Following fields are now mandatory: `Document.number`, `KycInfo.contacts`. Please consult documentation for more information.
- `supportedCountryCodes` moved to `CountryCodes.supported`.

### Added
- `CountryCodes.isSupportedCountry()` helper function to validate that the provided country is supported by Fourthline backend.
- `INVALID_BIRTH_COUNTRY_CODE` validation error.

## [2.2.7] - 08-04-2021
### Fixed
- User gets stuck on liveliness check if the head movement is not completed before the timeout occurs.

### Changed
- Address street name logic in KYC module.
- Email validation logic.

### Added
- OCR when image is taken manually.

## [2.2.6] - 24-03-2021
### Fixed
- Compile time issues when integrating NFC scanner in projects with Dagger 2.
- Preview freeze after Vision scanners complete successfully.
- Parsing nationality of TD1 documents.

### Changed
- `Address.streetNumber`, `Document.number` and `Attachment.timestamp` validation rules, please check documentation for more information.

### Removed
- Obsolete `INVALID_REGION` validation error from `Address` container.
- Obsolete `INVALID_BIRTH_PLACE` validation error from `Person` container.
- Jetifier.

### Added
- Javadoc / KDoc. It can be found at [Github](https://github.com/Fourthline-com/FourthlineSDK-Android) under `documentation` folder.

## [2.2.5] - 12-03-2021
### Fixed
- Bug introduced in 2.2.4 when the Document scanner completes instead of switching to the next step when using some specific flows.
- Intelligent video trimmer for Selfie scanner.

### Changed
- Kotlin to `1.4.31`, coroutines to `1.4.2` and other dependencies to the latest available.

## [2.2.4] - 11-03-2021
### Note: this version contains a bug in the Document scanner. Please use SDK version 2.2.5 or higher.

### Changed
- Validation rules in KYC module are now aligned with the Fourthline backend validation rules.
- Koin was replaced with Dagger 2.

### Fixed
- Camera preview artifacts on some phones.
- Vision scanners background from white to black, so there is not blinking upon preview dismiss.
- Memory leaks in Vision module.

## [2.2.3] - 23-02-2021
### Fixed
- Crash upon creating zip file on Android 6.
- Scanner fails with `RECORDING_FAILED` on some devices when `shouldRecordVideo = true` in Vision Scanners.
- Scanner cannot initialise on some devices when `shouldRecordVideo = true` in Vision Scanners.

## [2.2.2] - 10-02-2021
### Changed
- Maximum kyc zip size is increased to 100MB

### Fixed
- Rare occurrence where a scanner video would be longer than 10 seconds.

## [2.2.1] - 28-01-2021
### Changed
- `minSdkVersion` is set to `23`

## [2.2.0] - 28-12-2020
### Added
- Scanners as fragments.
- Support for data groups 11 (personal details) and 12 (document details) in NFC scanner.

### Fixed
- Crash of Vision Scanners when ML Kit misbehaves and returns unexpected values.

## [2.1.2] - 23-11-2020
### Changed
- `location` field of `DeviceMetadata` is now public in order to allow sdk-users to provide location.

### Added
- Support for MRTD and IDL passive authentication. SDK returns all necessary data for performing authentication inside of `NfcScannerResult.retrievedData`.

### Fixed
- Crash when face, ocr and/or barcode models are missing, instead error or warning is returned.
- Crash when SDK tries to consume more than 5 images from the camera stream.
- Added missing location permission to `AndroidManifest.xml` of `fourthline-core` module.
- z-ordering of views in vision scanners: debug rectangles were placed on top of overlay view, so they are visible even if overlay is opaque.

## [2.1.1] - 12-10-2020
### Changed
- `compileSdkVersion` and `targetSdkVersion` to the latest API (30) as well as all dependencies are now up-to-date.
- `Jpeg2000` image (face info from MRTD NFC chip) decoding to bitmap to happen fully in-memory, so the temp file is not created / stored in filesystem during the decoding process.
- Removed support for documents where the access to NFC chip data does not require authentication

### Fixed
- Improved handling of optional data in MRZ TD 1 documents that is used for total checksum calculations.

## [2.1.0] - 15-09-2020
### Changed
- `MrzInfo` declaration from interface to sealed class for better and easier handling of subclasses.
- `NfcScannerResult` to better represent various data stored on different types of documents. Please see `Removed` section.
- `NfcScannerSecurityKey.create()` functions were replaced with equivalents (`createWithMrtdData()`,`createWithMrtdCanNumber()`).
- Steps `READING_MRZ_INFO` and `READING_PHOTO` from `NfcScannerStep` were replaced with `READING_DATA`, since not all documents have MRZ on the NFC chip.
- `MrtdMrzInfo.create()` was moved to the base class (`MrzInfo`).

### Added
- `IdlMrzInfo` class that represents data stored in MRZ of Drivers Licenses as well as set of `IdlMrzInfoValidationError`.
- `IdlBasicInfo` class that represents data stored in NFC chip of Drivers Licenses.
- `NfcData` and `NfcDataGroup` that represents various data retrieved from the NFC chip.
- `NfcScannerSecurityKey.createWithIdlMrz()` to support reading IDL NFC chip data.
- `DUTCH_DRIVERS_LICENSE` type was added to `DocumentType` to enable Dutch IDL specific logic.
- `Attachment.Nfc` was extended with an additional parameter `dataGroups` to support sending data read from NFC chip to the back end.
- `isEnabled` field to `NfcScanner` to further simplify clients code.

### Fixed
- Possible collision in the names of stored video files, a new random UUID is generated for each file.
- `Person` container validation to mitigate the risks of incorrectly comparing dates ±1 second on midnight.
- `NfcScanner.onStepUpdate()` to propagate `LOOKING_FOR_DOCUMENT` correctly.

### Removed
- `mrzInfo` and `photo` fields from `NfcScannerResult`. Please use `NfcScannerResult.getData()` instead, e.g. `result.getData(type = NfcData.NfcDataType.PHOTO)`.

## [2.0.2] - 26-07-2020
### Fixed
- Timeout was properly propagated for any manual step of document scanner.
- Leftover logging was removed.
- Selfie and document videos that are packed into zip file now have random unique UUIDs to prevent collisions.

## [2.0.1] - 06-07-2020
### Fixed
- Improved performance of MRZ detector inside of the Document Scanner.
- Improved KYC module interoperability with Java: more constructors available and `ZipperError` can
be handled gracefully.
- Total checksum calculations for TD2 and TD3 were updated to properly work with different document
number length.
- Improved threading policy: offload work from camera2 thread.

## [2.0.0] - 25-06-2020
### Changed
- `MrzInfo` was renamed to `MrtdMrzInfo` and became a child of `MrzInfo` to reduce the impact on
 public API's for future scalability.

### Removed
- `arrow-kt` dependency is removed from the SDK (NFC and Vision modules).

## [2.0.0-beta07] - 17-06-2020
### Changed
- fixed colors are used for debug rectangles in vision scanners.
- migrate vision module from `firebase-ml-vision` to standalone `com.google.mlkit` to enforce and
 ensure that only on-device capabilities are used.
- KYC validation is more relaxed: the required containers are `Provider` and `Person` (only
 `firstName`, `lastName` and `birthDate` are mandatory to fill). All other containers are optional
 and are validated only when corresponding fields are set.

### Fixed
- crash in `XmlBuilder` of KYC module when ip address is not provided to `DeviceMetadata` container.
- document scanner preview lagging on some devices (e.g. Huawei P30) when `shouldRecordVideo` is set
 to false.
- provided detection area coordinates to vision scanners not matching to what user sees on the
 screen on some devices (e.g. Sony Xperia E5823).
- selfie scanner's `MANUAL_SELFIE` step had no timeout.
- crash in selfie scanner when `takeSnapshot()` is called outside of `MANUAL_SELFIE` step.