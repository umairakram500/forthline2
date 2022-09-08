# Android Fourthline SDK

* [Overview](#overview)
* [Integration](#integration)
* [Fourthline Core](#fourthline-core)
    * [Location Provider](#location-provider)
        + [Example usage](#example-usage-1)
    * [Analytics](#fourthline-analytics)
        + [Enabling Fourthline Analytics](#enabling-fourthline-analytics)
        + [Analytics Tracking Consent](#analytics-tracking-consent)
        + [Example usage](#example-usage-2)
* [Fourthline Vision](#fourthline-vision)
   * [Security note](#security-note-lock)
   * [ML Kit models note](#ml-kit-models-note)
   * [Selfie scanner](#selfie-scanner)
        * [Example Usage](#example-usage-3)
   * [Document Scanner](#document-scanner)
        * [Example Usage](#example-usage-4)
* [Fourthline NFC](#fourthline-nfc)
   * [NFC Scanner](#nfc-scanner)
        * [Example Usage](#example-usage-5)
* [Fourthline KYC](#fourthline-kyc)
   * [KycInfo](#kycinfo)
        * [Example Usage](#example-usage-6)
   * [Zipper](#zipper)
        * [Example Usage](#example-usage-7)
* [Troubleshooting](#troubleshooting)

## **Overview**
Fourthline SDK contains different modules that can be integrated as stand alone SDKs.

- fourthline-core
- fourthline-vision
- fourthline-nfc
- fourthline-kyc

Fourthline SDK requires minimum API version 23.


## **Integration**

You can add desired modules of Fourthline SDK to your project using Gradle.

In your **top level** `build.gradle` file, link to Fourthline's repository:
###### Gradle
```groovy
allprojects {
    repositories {
        //...
        maven {
            url "https://maven.pkg.github.com/Fourthline-com/FourthlineSDK-Android"
            credentials {
                username ""
                password getLocalProperty("github_token")
            }
        }
    }
}
```

###### Gradle KTS
```kotlin
allprojects {
    repositories {
        //...
        maven {
            url = URI("https://maven.pkg.github.com/Fourthline-com/FourthlineSDK-Android")
            credentials {
                username = ""
                password = getLocalProperty("github_token")
            }
        }
    }
}
```

Inside your Github account:
- Go to `Settings` -> `Account security` -> `Enable two-factor authentiation`. This is required in order to access Fourthline repository.
- Go to `Settings` -> `Developer Settings` -> `Personal Access Tokens` -> `Generate a new Token`
- Select the following scope: "read:packages" and generate a new token
- Store the access token in a secure, but accessible location, e.g. `local.properties`. Treat this token as a password - do not add it to your version control system and if it has to be shared, use a password manager.

###### local.properties
```
//..
github_token=YOUR_ACCESS_TOKEN
```

In your module level `build.gradle` file, add a dependency to Fourthline SDK:

###### Gradle
```groovy
dependencies {
    // ...
    def fourthlineSdkVersion = "2.14.0"
    implementation "com.fourthline:fourthline-vision:$fourthlineSdkVersion"
    implementation "com.fourthline:fourthline-nfc:$fourthlineSdkVersion"
    implementation "com.fourthline:fourthline-core:$fourthlineSdkVersion"
    implementation "com.fourthline:fourthline-kyc:$fourthlineSdkVersion"
}
```

###### Gradle KTS
```kotlin
dependencies {
    // ...
    val fourthlineSdkVersion = "2.14.0"
    implementation("com.fourthline:fourthline-vision:$fourthlineSdkVersion")
    implementation("com.fourthline:fourthline-nfc:$fourthlineSdkVersion")
    implementation("com.fourthline:fourthline-core:$fourthlineSdkVersion")
    implementation("com.fourthline:fourthline-kyc:$fourthlineSdkVersion")
}
```

## Fourthline Core
### Location Provider
`LocationProvider` is a wrapper around native `FusedLocationProviderClient` which simplifies interaction with location services and hides complexity of interaction with different OS versions.

#### Example usage

```kotlin
fun requestLocation() {
    val locationProvider = FourthlineLocationHelper.getLocationProvider(this)
    with(locationProvider) {
        requestAuthorization({
            requestFullAccuracy({
                requestLocation {
                    kycInfo.metadata.location = it
                }
            }, {
                //handle
            })
        }, {
            //handle
        })
    }
}
```

## Fourthline Analytics

It requires the [Datadog](https://github.com/DataDog/dd-sdk-android) dependency to be available at runtime.

### Adding Datadog dependency

###### Gradle
```groovy
dependencies {
    // ...
    implementation "com.datadoghq:dd-sdk-android:1.10.0"
}
```

###### Gradle KTS
```kotlin
dependencies {
    // ...
    implementation("com.datadoghq:dd-sdk-android:1.10.0")
}
```

### Enabling Fourthline Analytics

Analytics data collection is **disabled by default**, no data is being collected or sent to Fourthline.

Enabling analytics data collection and reporting is a **two-step process**:

1. Initialize analytics by calling `FourthlineAnalytics.initialize(TENANT_ID, context)` when suitable, usually in `Application.onCreate()` using the tenant id provided by Fourthline.

**Note:** `FourthlineAnalytics.initialize` call throws `AnalyticsError`.

Possible throwable types:

| Type                           | Description                       |
|:-------------------------------|:----------------------------------|
| `AnalyticsError.InvalidTenantId`    | Invalid tenant id. Please ensure you are using the analytics tenant id provided by Fourthline. |
| `AnalyticsError.DatadogNotImported` | Datadog dependency is not available at runtime. |

2. Set `TrackingConsent` to `GRANTED` by calling ` FourthlineAnalytics.setTrackingConsent(TrackingConsent.GRANTED)`.

### Analytics Tracking Consent

By default, **each time** `FourthlineAnalytics` is initialized with a valid tenant id, `TrackingConsent` is set to `PENDING`.

`TrackingConsent` can have one of the following values:
- `PENDING` - the SDK starts collecting and batching the data but does not send it to Fourthline. The SDK waits for the new tracking consent value to decide what to do with the batched data.
- `GRANTED` - the SDK starts/continues collecting the data and sends it to Fourthline.
- `NOT_GRANTED` - no new data is being collected or sent to Fourthline. Any data, previously collected while consent was set to `PENDING`, is removed.

To change the tracking consent value after the SDK is initialized, use  `FourthlineAnalytics.setTrackingConsent`. The SDK changes its behavior according to the new value. For example, if the current tracking consent is `PENDING` and it changes to:
- `GRANTED` - the SDK will send all current and future data to Fourthline.
- `NOT_GRANTED` - the SDK will wipe all current data and will not collect any future data.

### Debug builds

`FourthlineAnalytics` is used to capture production analytics data.\
It does not collect or send any analytics data when `ApplicationInfo.flags` has `FLAG_DEBUGGABLE`.

#### Example Usage
<details>
 <summary>Kotlin</summary>

```kotlin
try {
    FourthlineAnalytics.initialize(tenantId = "<Id provided by Fourthline>", context = applicationContext)
} catch (e: AnalyticsError) {
    when (e) {
        is AnalyticsError.DatadogNotImported -> TODO()
        is AnalyticsError.InvalidTenantId -> TODO()
    }
}

FourthlineAnalytics.setTrackingConsent(consent = TrackingConsent.GRANTED)
```
</details>

<details>
  <summary>Java</summary>

```java
try {
    FourthlineAnalytics.initialize("<Id provided by Fourthline>", applicationContext);
} catch (AnalyticsError e) {
    //TODO()
}

FourthlineAnalytics.setTrackingConsent(TrackingConsent.GRANTED);
```
</details>

###### Permissions

###### Location

SDK requires applications to have at least `ACCESS_COARSE_LOCATION` (`ACCESS_FINE_LOCATION` is preferred) permission.

###### Camera

`CAMERA` permission is required in order to make SDK scanners work as scanners use camera feed for detections and recording.

## **Fourthline Vision**

### Security note :lock:
The Scanner components of the SDK (Selfie scanner and Documents scanner) are used to gather visual data regarding the identity of the end user. The SDK makes static pictures of both the person and their identification documents, and at the same time, records short up to 10 seconds videos, if the "recordingType" configuration parameter is turned ON (the parameter is enabled by default).
<br>Please see `SelfieScannerConfig` and `DocumentScannerConfig`.

In opposite to the static pictures, which are always held in run-time memory, the videos are stored in a temporary storage on the end user’s mobile device’s hard drive. This is due to the fact that the video data must remain available until it is collected by the KYC module and submitted to the SDK user’s backend.

The sensitive data can be downloaded by either a computer virus on a computer that is trusted and connected to the mobile device, or a malicious user with physical access to a jailbroken or rooted device. It is therefore the responsibility of the SDK user (consumer) to properly dispose of the recorded videos once they are not required anymore.

### ML Kit models note
The Fourthline SDK uses the approach recommended by Google for automatically downloading the latest models by using thin version of ML Kit libraries and modifying the `AndroidManifest.xml` as described in more details [here](https://developers.google.com/ml-kit/vision/face-detection/android), [here](https://developers.google.com/ml-kit/vision/text-recognition/android) and [here](https://developers.google.com/ml-kit/vision/barcode-scanning/android). Documentation states that:
> You can configure your app to automatically download the ML model to the device after your app is installed from the Play Store.

However, in some specific cases models are missing at the time scanners start which results in Selfie and/or Document scanners not working in automatic mode. SDK users has the following options to handle such situations:
1. Compile your app using bundled versions of Face and/or Barcode libraries, due to the fact that Text recognition library [only available in thin variant](https://developers.google.com/ml-kit/migration/android#vision_apis). To do this you need to exclude thin library variants and add dependencies on bundled library variants as shown below:

###### Gradle
```groovy
dependencies {
    // ...
    implementation("com.fourthline:fourthline-vision:$fourthlineSdkVersion") {
        exclude group: "com.google.android.gms", module: "play-services-mlkit-face-detection"
        exclude group: "com.google.android.gms", module: "play-services-mlkit-barcode-scanning"
    }

    implementation "com.google.mlkit:face-detection:$mlKitVersion"
    implementation "com.google.mlkit:barcode-scanning:$mlKitVersion"
}
```

###### Gradle KTS
```kotlin
dependencies {
    // ...
    implementation("com.fourthline:fourthline-vision:$fourthlineSdkVersion") {
        exclude(group = "com.google.android.gms", module = "play-services-mlkit-face-detection")
        exclude(group = "com.google.android.gms", module = "play-services-mlkit-barcode-scanning")
    }

    implementation("com.google.mlkit:face-detection:$mlKitVersion")
    implementation("com.google.mlkit:barcode-scanning:$mlKitVersion")
}
```
**Note:** using bundled variants result in increased apk size. The current version of Face model is about 16MB, Barcode model is about 2MB.

 *Document scanner note:* In case you need to update the UI accordingly, for example to show snapshot button, listen to `RECOGNITION_MODELS_NOT_DOWNLOADED` warning inside of `DocumentScannerCallback.onWarnings()` on steps with `DocumentScannerStep.isAutoDetectAvailable = true`.

### Selfie scanner

`SelfieScannerActivity` and `SelfieScannerFragment` classes from Fourthline SDK handle selfie and liveness detection. It requires `fourthline-vision` framework to be added to your project.

- Make sure that user granted access to `CAMERA` and at least `ACCESS_COARSE_LOCATION` (`ACCESS_FINE_LOCATION` is preferred) permissions.

- Extend `SelfieScannerActivity` or `SelfieScannerFragment` and implement the required methods from `SelfieScanner`, `SelfieScannerDataSource` and `SelfieScannerCallback`.

- Scanning flow starts `onResume()` and stops `onPause()` of activity or fragment.

- Develop custom `View` which will be used as overlay view and provide it in `getOverlayView()` method from `SelfieScanner`. Selfie scanner will put it on top of camera streaming layer with `MATCH_PARENT` layout parameters.

- Data source will be asked for:
	- `getConfig()` Return instance of `SelfieScannerConfig` class with desired configuration properties.

    <br> Variables:
    | Variable                              | Type                                     | Default            | Description   |
    |:--------------------------------------|:-----------------------------------------|--------------------|---------------|
    | livenessCheckType                     | `LivenessCheckType`                      | **HEAD_TURN**      | Determines which liveness check `SelfieScanner` does  after **SELFIE** step.  |
    | debugModeEnabled                      | boolean                                  | **false**          | Defines if scanner will display debug view. |
    | recordingType                         | `RecordingType`                          | **VIDEO_ONLY**     | Defines if scanner will record and which type of recording it will perform. |


    | `LivenessCheckType`        | Description    |
    |:---------------------------|:---------------|
    | HEAD_TURN                  | Move to **TURN_HEAD_RIGHT** and **TURN_HEAD_LEFT** steps in random order after **SELFIE** step.  |


    | `RecordingType`      | Description    |
    |:---------------------|:---------------|
    | NONE                 | Do not record. |
    | VIDEO_ONLY           | Record only videos with no audio. <br>Exported as .mp4  |
    | VIDEO_WITH_AUDIO     | Record videos with audio in the same file. <br>Exported as .mp4 |

    <br></br>
	- `getFaceDetectionArea()` Return instance of `Rect` structure which represents frame in screen’s coordinate system. The face's bounding box should be inside *faceDetectionArea* in order to succeed **SELFIE** step.

- `SelfieScannerCallback` will be informed about:
	- `onStepUpdate(step: SelfieScannerStep)` -> moving to next step.
    <br>This is perfect time to modify your custom overlay view if needed.
    <br>Step values:

        | `SelfieScannerStep`          | Description     |  Timeout        |
        |:-----------------------------|:----------------|:----------------|
        | SELFIE                       | - required step<br>- will occur as first step.<br>- `SelfieScanner` will emit warnings for all conditions which are not met | 15 seconds |
        | TURN_HEAD_LEFT               | - required step<br>- will occur if `livenessCheck` from `SelfieScannerConfig` is set to **HEAD_TURN**<br>- completes after user turns head to the left | 15 seconds |
        | TURN_HEAD_RIGHT              | - required step<br>- will occur if `livenessCheck` from `SelfieScannerConfig` is set to **HEAD_TURN**<br>- completes after user turns head to the right | 15 seconds |

	- `onWarnings(warnings: List<SelfieScannerWarning>)` -> some conditions block the user proceeding to the next step.
    <br>This is perfect time to update your overlay view to help user proceed current step.
    <br>Warning values:

      | `SelfieScannerWarning`  | Description                                                |
      |:------------------------|:-----------------------------------------------------------|
      | FACE_NOT_DETECTED       | No faces were detected                                     |                         |
      | FACE_NOT_IN_FRAME       | Face bounding box is not fully inside `faceDetectionArea`  |
      | FACE_TOO_CLOSE          | Face area is pretty big compared to `faceDetectionArea`    |
      | FACE_TOO_FAR            | Face area is pretty small compared to `faceDetectionArea`  |
      | FACE_YAW_TOO_BIG        | Face is not looking straight at the camera                 |
      | DEVICE_NOT_STEADY       | Device shakes too much                                     |
      | EYES_CLOSED             | It occurs when the eyes are closed                         |

       All warnings are emitted ONLY during **SELFIE** step.

	- `onSuccess(result: SelfieScannerResult)` -> the scanning process succeed. This call is terminal for the scanner.
    <br>This is perfect time to to store results somewhere to pass it to our backend later as well as remove scanner from your view hierarchy and present success screen.

	- `onFail(error: SelfieScannerError)` -> the scanning process failed. This call is terminal for the scanner.
    <br>This is perfect time to remove scanner from your view hierarchy and present failure screen.
    <br>Error values:

      | `SelfieScannerError`               | Description                               |
      |:-----------------------------------|:--------------------------------------------------|
      | TIMEOUT                            | Current step timed-out.                           |
      | FACE_DISAPPEARED                   | Face disappeared during liveness check.           |
      | CAMERA_PERMISSION_NOT_GRANTED      | Access to Camera has not been granted.            |
      | RECORD_AUDIO_PERMISSION_NOT_GRANTED| Access to Microphone has not been granted.<br>Can be returned only when `SelfieScannerConfig.recordingType` is set to `VIDEO_WITH_AUDIO`.<br>Make sure the user granted `RECORD_AUDIO` permission.  |
      | CAMERA_NOT_AVAILABLE               | Phone is missing a suitable hardware camera or Android thinks that a suitable hardware camera is unavailable, for example when camera is offline. |
      | MULTIPLE_FACES_DETECTED            | Multiple faces were detected.                     |
      | RECORDING_FAILED                   | There is a problem occurred during recording or processing the video or the audio.<br>Will not occur if `recordingType` variable from `SelfieScannerConfig` is set to **NONE**. |
      | SCANNER_INTERRUPTED                | Any kind of interruption occurred while scanner is active (app in background, call, alert, etc). |
      | RECOGNITION_MODELS_NOT_DOWNLOADED  | Face model is missing from the device.            |
      | GOOGLE_PLAY_SERVICES_NOT_AVAILABLE | Google Play services are not installed or not enabled on this device or that the version installed is outdated. |
      | UNKNOWN                            | Unexpected error occurred. Please report this issue immediately. |

    Callback are invoked on arbitrary thread, so in order to update the UI wrap the code into `runOnUiThread()`.


#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
class SelfieActivity : SelfieScannerActivity() {

	// ... Activity which is going to present Selfie Scanner
 	companion object {
        fun start(context: Context) = with(context) {
            Intent(this, SelfieActivity::class.java).let(::startActivity)
        }
    }

	// ... Selfie Scanner Data Source
    override fun getFaceDetectionArea(): Rect = with(img_detection_area) {
        Rect(left, top, right, bottom)
    }
	// set properties of config object or do nothing and use default recommended values
    override fun getConfig(): SelfieScannerConfig = SelfieScannerConfig()

	//Set your overlay view
    override fun getOverlayView(): View = LayoutInflater
        .from(this)
        .inflate(R.layout.overlay_selfie, findViewById(android.R.id.content), false)

	// ... Selfie Scanner Callbacks
	 override fun onStepUpdate(step: SelfieScannerStep) = runOnUiThread {
	    // update your overlay view or assign another overlay view to overlayView property
    }

	override fun onWarnings(warnings: List<SelfieScannerWarning>) = runOnUiThread {
       	// update your overlay view to help user proceed current step
    }

    override fun onSuccess(result: SelfieScannerResult) = runOnUiThread {
        // save result somewhere to send it later to our BE
    	// present success screen with `result.image.cropped` image
    }

    override fun onFail(error: SelfieScannerError) = runOnUiThread {
        // present error screen
    }
}

```
</details>

### **Document Scanner**
`DocumentScannerActivity` and `DocumentScannerFragment` classes from Fourthline SDK handle document pages photo making and machine readable zone (MRZ) reading, if available. Document scanner supports flow with presenting to user results of each page scan before proceeding to next step. It requires `fourthline-vision` framework to be added to your project.

- Make sure that user granted access to `CAMERA` and at least `ACCESS_COARSE_LOCATION` (`ACCESS_FINE_LOCATION` is preferred) permissions.

- Extend `DocumentScannerActivity` or `DocumentScannerFragment` and implement the required methods from `DocumentScanner`, `DocumentScannerDataSource` and `DocumentScannerCallback`.

- Scanning flow starts `onResume()` and stops `onPause()` of activity or fragment.

- Develop custom `View` which will be used as overlay view and provide it in `getOverlayView()` method from `DocumentScanner`. Document scanner will put it on top of camera streaming layer with `MATCH_PARENT` layout parameters.

- Data source will be asked for:
	- `getConfig()` Return instance of `DocumentScannerConfig` class with desired configuration properties.

    <br> Variables:
    | Variable                       | Type                                     | Default                          | Description   |
    |:-------------------------------|:-----------------------------------------|----------------------------------|---------------|
    | type                           | `DocumentType`                           | **Required Parameter**           | `DocumentScanner` determines steps based on document type. |
    | mrzValidationPolicy            | `MrzValidationPolicy`                    | **STRONG**                       | `DocumentScanner` rejects any MRZ which does not compliant with policy. <br>It will return best MRZ candidate retrieved in reasonable time.|
    | validationConfig               | `DocumentValidationConfig`               | See `DocumentValidationConfig`   | `DocumentScanner` performs document step data validation based on this configuration object. |
    | includeAngledSteps             | boolean                                  | **true**                         | `DocumentScanner` includes angled step after each non-angled step when this variable is set to **true**.|
    | debugModeEnabled               | boolean                                  | **false**                        | Defines if scanner will display debug view. |
    | videoDuration                  | `VideoDuration`                          | **DEFAULT**                      | Defines the maximum duration of a video recording. |


    | `DocumentType`        | Description           |
    |:----------------------|:----------------------|
    | PASSPORT              | Passport              |
    | ID_CARD               | ID Card               |
    | DRIVERS_LICENSE       | Drivers License       |
    | RESIDENCE_PERMIT      | Residence Permit      |
    | PAPER_ID              | Paper ID              |
    | FRENCH_ID_CARD        | French ID Card<br>⚠️ &nbsp;Has its own document type because it has a format that differs from the national ID card common specification |
    | DUTCH_DRIVERS_LICENSE | Dutch Drivers License |
    | PROOF_OF_ADDRESS      | Proof of address      |


    | `MrzValidationPolicy`         | Description    |
    |:------------------------------|:---------------|
    | STRONG                        | No validation errors are allowed.  |
    | NORMAL                        | One validation error is allowed (totalChecksum does not count). |
    | WEAK                          | Three validation error are allowed (totalChecksum does not count). |


    | `VideoDuration`      | Description    |
    |:---------------------|:---------------|
    | DEFAULT              | Record video with default duration. Only use this setting if previously agreed upon with Fourthline. |
    | EXTENDED             | Record video with extended duration. Only use this setting if previously agreed upon with Fourthline. |

    ##### Steps specifics:
    Document scanner has 40 seconds timeout for any step.

    **DUTCH_DRIVERS_LICENSE**
    The Dutch Drivers Licence flow consists of two or four steps (if scanner is launched with `DocumentScannerConfig.isAngled = true`). For the front step, scanner is looking for MRZ at the bottom of the provided detection area and automatically completes the step when MRZ is recognized. The back step should be done manually, however, frame is analysed before returning the result. The scanner tries to find and decode QR code from the back side of the Drivers Licence. In case when QR is found and decoded successfully and if the parsed MRZ contains zero or less mistakes than the MRZ saved from scanning the front side, the MRZ retrieved from the QR code is returned by the scanner in the `DocumentScannerResult`.

    **PROOF_OF_ADDRESS**
    The Proof of address flow should be launched with `DocumentScannerConfig.isAngled = false` and `DocumentScannerConfig.recordingType = RecordingType.NONE`. In case several sides need to be scanned, the scanner should be relaunched until all pages are scanned.

	- `getDocumentDetectionArea()` Return instance of `Rect` structure which represents frame in screen’s coordinate system which should contain document page.
	 This value will be used to crop image before returning it to you (you will receive full image as well).
	 <br>You are highly advised to use frame with correct aspect ratio for document (height/width) because MRZ detection area is calculated based on document detection area:

        | Frame Area type       | Aspect Ratio(h/w)   |
        |:----------------------|:-------------------:|
        | Passport              | 0.704               |
        | National ID card      | 0.631               |
        | Residence Permit      | 0.631               |
        | Drivers License       | 0.631               |
        | Dutch Drivers License | 0.631               |
        | French ID card        | 0.705               |
        | Paper ID              | 1.364               |

         MRZ Detection Height -> a percentage of the detection frame height starting from the bottom.

        | Frame Area type       | MRZ Detection height % |
        |:----------------------|:----------------------:|
        | Passport              | 0.264                  |
        | National ID card      | 0.43                   |
        | Residence Permit      | 0.43                   |
        | Dutch Drivers License | 0.225                  |
        | French ID card        | 0.314                  |

- Callbacks will be informed about:
	- `onStepUpdate(step: DocumentScannerStep)` -> moving to the next step.
    <br>This is perfect time to modify your custom overlay view by assigning it to *getOverlayView()* method.
    <br>Document scanner step is combination of `DocumentFileSide`

        | `DocumentFileSide` | Description                       |
        |:-------------------|:----------------------------------|
        | FRONT              | Front side of the document        |
        | BACK               | Back side of the document         |
        | INSIDE_LEFT        | Inside left side of the paper ID  |
        | INSIDE_RIGHT       | Inside right side of the paper ID |

        and fields

        | Fields                | Description                       |
        |:----------------------|:----------------------------------|
        | isAngled              | Boolean determining if document page should be tilted (some security features can be seen only when document is tilted) |
        | isAutoDetectAvailable | Boolean determining if `DocumentScanner` will try to scan page automatically<br>💡 We advise to hide snapshot button when this property is set to **TRUE** and show it after some timeout (10-15 seconds), assuming that user cannot do automatic scan of document page with MRZ |
        | index                 | Integer representing the step position in the total number of document scanner steps.|

	- `onWarnings(warnings: List<DocumentScannerStepWarning>)` -> some conditions block user from proceeding to the next step.
    <br>This is perfect time to update your overlay view to help user proceed current step.
    <br>Warnings set may contain next values:

      | `DocumentScannerStepWarning`       | Description                         |
      |:-----------------------------------|:------------------------------------|
      | DOCUMENT_TOO_DARK                  | Images from the camera are too dark |
      | DEVICE_NOT_STEADY                  | Device shakes too much              |
      | RECOGNITION_MODELS_NOT_DOWNLOADED  | OCR or barcode models are missing from the device. |
      | GOOGLE_PLAY_SERVICES_NOT_AVAILABLE | Google Play services are not installed or not enabled on this device or that the version installed is outdated |

	- `onStepSuccess(result: DocumentScannerStepResult)` -> succeeding scanning of current document step.
    <br>This is perfect time to present step scan results to user or immediately proceed to next step by calling `moveToNextStep` method (allowed to use only after current step succeed).
    <br>If you want to restart current step scanning process for any reasons, call `restartCurrentStep` method (allowed to use only after current step failed or succeed).
    <br>Can be triggered either by either automatic MRZ detection or by calling `takeSnapshot` method (allowed to use only when current step is progressing). Don't forget to store full image somewhere to pass it to our backend later.
    <br>💡 &nbsp; Don't forget to store full image somewhere to pass it to our BE later.
    <br>We recommend to inform user about the issues and guide them into retaking the picture in case `stepResult.validationErrors` set is not empty. It can contain next values:

    | `DocumentValidationError`       | Description                         |
    |:--------------------------------|:------------------------------------|
    | ISSUING_COUNTRY_NOT_SUPPORTED   | Issuing country is not supported (See `DocumentValidationConfig` for details). |
    | DOCUMENT_TYPE_NOT_SUPPORTED     | Document type is not supported (See `DocumentValidationConfig` for details). |
    | NATIONALITY_NOT_SUPPORTED       | Nationality is not supported (See `DocumentValidationConfig` for details). |
    | DOCUMENT_EXPIRED                | Document is expired.                |
    | PERSON_NOT_ADULT                | Person is not adult (See `DocumentValidationConfig` for details). |
    | DOCUMENT_TYPE_INVALID           | Scanned document has different MRZ type than selected document type. |
    | MRZ_NOT_VALID                   | Detected MRZ contains validation errors (usually OCR mistakes). |
    | MRZ_NOT_DETECTED                | MRZ was not detected but was expected for current step. |
    | PHOTO_DETECTED                  | Currently not implemented.          |
    | PHOTO_NOT_DETECTED              | Currently not implemented.          |

	- `onStepFail(error: DocumentScannerStepError)` -> scanning process of current document step failed.
    <br>This situation should not appear in case you're not violating business logic of scanner's API.
    <br>Possible values:

    | `DocumentScannerStepError`     | Description                                               |
    |:-------------------------------|:----------------------------------------------------------|
    | TAKE_SNAPSHOT_NOT_ALLOWED      | `takeSnapshot()` is called and step is not active anymore |
    | MOVE_TO_NEXT_STEP_NOT_ALLOWED  | `moveToNextStep()` is called when the step is not successfully finished |
    | RESET_CURRENT_STEP_NOT_ALLOWED | `resetCurrentStep()` is called and step is still active   |
    | INVALID_CONTENT_DETECTION_AREA | `getDocumentDetectionArea()` returns an invalid rectangle which prevents document detection |


	- `onSuccess(result: DocumentScannerResult)` -> the scanning process succeed. This call is terminal for the scanner.
    <br>This is perfect time to to store results somewhere to pass it to our BE later as well as to remove scanner from your view hierarchy and present success screen.

	- `onFail(error: DocumentScannerError)` -> the scanning process failed. This call is terminal for the scanner.
   <br>This is perfect time to remove scanner from your view hierarchy and present failure screen.
   <br>Possible error values: **TIMEOUT**, **CAMERA_PERMISSION_NOT_GRANTED**, **RECORDING_FAILED**, **SCANNER_INTERRUPTED**, **UNKNOWN**.

        | `DocumentScannerError`             | Description                                               |
        |:-----------------------------------|:----------------------------------------------------------|
        | TIMEOUT                            | Current step timed-out.                                   |
        | CAMERA_PERMISSION_NOT_GRANTED      | Access to Camera has not been granted.                    |
        | RECORD_AUDIO_PERMISSION_NOT_GRANTED| Access to Microphone has not been granted.<br>Can be returned only when `DocumentScannerConfig.recordingType` is set to `VIDEO_WITH_AUDIO`.<br>Make sure the user granted `RECORD_AUDIO` permission.  |
        | CAMERA_NOT_AVAILABLE               | Phone is missing a suitable hardware camera or Android thinks that a suitable hardware camera is unavailable, for example when camera is offline. |
        | RECORDING_FAILED                   | There is a problem occurred during recording or processing the video or the audio.<br>Will not occur if `recordingType` variable from `DocumentScannerConfig` is set to **NONE**. |
        | SCANNER_INTERRUPTED                | Any kind of interruption occurred while scanner was active (background, call, alert etc) |
        | UNKNOWN                            | Unexpected error occurred. Please report this issue immediately |

   - `onStepsCountUpdate(count: Int)` **function with default implementation**
    <br> The document scanner has updated the total number of steps comprising the document scan journey.
    <br> You can use this information to present a progress bar throughout the scan process, providing even more clarity to the user during their document scan journey.
    <br> You can access the total number of document steps by using the document scanner's `stepCount` field.
    <br> The current step's `index` can be retrieved from the `DocumentScannerStep` object received during `onStepUpdate(step: DocumentScannerStep)`.


    Callbacks are invoked on arbitrary thread, so in order to update the UI wrap the code into `runOnUiThread()`.

#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
class DocumentActivity : DocumentScannerActivity() {

	// ... Activity which is going to present Document Scanner
    companion object {
        fun start(context: Context) = with(context) {
            Intent(this, DocumentActivity::class.java).let(::startActivity)
        }
    }

	// ... Document Scanner Data Source
    override fun getDocumentDetectionArea(): Rect = with(documentDetectionArea) {
         Rect(left, top, right, bottom)
    }

	// set properties of config object or do nothing and use default recommended values
    override fun getConfig() = DocumentScannerConfig(type = ID_CARD)

	//Set your overlay view
    override fun getOverlayView(): View = LayoutInflater.from(this)
        .inflate(R.layout.overlay_document, findViewById(android.R.id.content), false)
    
	// ... Document Scanner Callback
    override fun onStepsCountUpdate(count: Int) {
        // update progress bar representing the total number of document scanner steps.
    }
    
    override fun onStepUpdate(step: DocumentScannerStep) = runOnUiThread {
        // update your overlay view or assign another overlay view to overlayView property
    }

    override fun onWarnings(warnings: List<DocumentScannerStepWarning>) = runOnUiThread {
   	    // update your overlay view to help user to proceed current step
    }

    override fun onStepFail(error: DocumentScannerStepError) = runOnUiThread {
        Log("Incorrect API usage, this never suppose to happen")
    }

    override fun onStepSuccess(result: DocumentScannerStepResult) = runOnUiThread {
	    // save results somewhere to pass it to our BE later
    	moveToNextStep()

	    // ..Or present overlay view with result.image.cropped with options to continue (call moveToNextStep()) or retry (call restartCurrentStep())...
    }

    override fun onFail(error: DocumentScannerError) = runOnUiThread {
  	    // present error screen
    }

    override fun onSuccess(result: DocumentScannerResult) = runOnUiThread {
        // save result somewhere to send it later to our BE
    	// present success screen
    }
}

```
</details>
<details>
  <summary>Java</summary>

```java
public class DocumentActivity extends DocumentScannerActivity {
    @NotNull
    @Override
    public DocumentScannerConfig getConfig() {
        return new DocumentScannerConfig(ID_CARD, true, false, true, STRONG);
    }

    @NotNull
    @Override
    public Rect getDocumentDetectionArea() {
        return new Rect(
                documentMask.getLeft(),
                documentMask.getTop(),
                documentMask.getRight(),
                documentMask.getBottom()
        );
    }

    @Nullable
    @Override
    public View getOverlayView() {
        return LayoutInflater.from(this)
                .inflate(R.layout.overlay_document, findViewById(android.R.id.content), false);
    }

    @Override
    public void onStepsCountUpdate(@NotNull int count) {
        runOnUiThread(() -> {
            // update progress bar representing the total number of document scanner steps.
        });
    }
    
    @Override
    public void onStepUpdate(@NotNull DocumentScannerStep step) {
        runOnUiThread(() -> {
            // update your overlay view or assign another overlay view to overlayView property
        });
    }

    @Override
    public void onWarnings(@NotNull List<? extends DocumentScannerStepWarning> warnings) {
        runOnUiThread(() -> {
            // update your overlay view to help user to proceed current step
        });
    }

    @Override
    public void onStepFail(@NotNull DocumentScannerStepError error) {
        runOnUiThread(() -> {
            Log("Incorrect API usage, this never suppose to happen");
        });
    }

    @Override
    public void onStepSuccess(@NotNull DocumentScannerStepResult result) {
        runOnUiThread(() -> {
            // save results somewhere to pass it to our BE later
            moveToNextStep();

            // ..Or present overlay view with result.getImage().getCropped() with options to continue (call moveToNextStep()) or retry (call restartCurrentStep())...
        });
    }

    @Override
    public void onFail(@NotNull DocumentScannerError error) {
        runOnUiThread(() -> {
            // present error screen
        });
    }

    @Override
    public void onSuccess(@NotNull DocumentScannerResult result) {
        runOnUiThread(() -> {
            // save result somewhere to send it later to our BE
            // present success screen
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, DocumentActivity.class);
        context.startActivity(intent);
    }
}
```
</details>

### Note
`View` that will be provided in `getOverlayView()` will be attached to scanner layout. That is why we expect provided `View` to be detached from parent. In given examples inflated `View` is not attached.

## **Fourthline NFC**

### **NFC Scanner**

`NFCScannerActivity` and `NFCScannerFragment` classes from Fourthline SDK read information from the document's chip using NFC technology.
It requires `fourthline-nfc` framework to be added to your project.
The scanner needs NFC supported and available in the device and in the document (check value of `isEnabled` and `isSupported` variable).

- Make sure that user granted `ACCESS_FINE_LOCATION` permission before launching the scanner.

- Extend `NfcScannerActivity` or `NFCScannerFragment` and implement the required methods from `NfcScanner`, `NfcScannerDataSource` and `NfcScannerCallback`.

- Implement methods: `onFail()` to get errors, `onSuccess()` to retrieve the result of the scanning, `onStepUpdate()` to get the step of the scanning and `getConfig()` to provide `NfcScannerSecurityKey`.

- `NfcScannerSecurityKey` is used to establish secure connection between phone and chip. There are several ways to create the key. It is recommended to create the key using `MrzInfo` information:
    - for MRTDs use `createWithMrtdData(document number, birth date and expiry date)` or `createWithMrtdCanNumber(number)` if the document supports it.
    - for IDLs use `createWithIdlMrz(mrz)`.

- Create instance of `NFCScannerConfig` class with `NFCScannerSecurityKey` object created at previous step.

- Scanning flow starts `onResume()` and stops `onPause()` of activity or fragment.

- Callbacks will be informed about:
	- `onStepUpdate(step: NfcScannerStep)` -> moving to the next step.
    <br>Possible *step* values:

      | `NFCScannerStep`       | Description                       |
      |:-----------------------|:----------------------------------|
      | LOOKING_FOR_DOCUMENT   | First step right after user start scanning until the tag is detected or error occurs |
      | CONNECTING_TO_DOCUMENT | Step after the tag is detected |
      | AUTHENTICATING         | Step while authenticating with the chip |
      | READING_DATA           | Step reading data information from the chip |

	- `onSuccess(result: NfcScannerResult)` -> succeeding the scanning process.
	<br>This is perfect time to to store results somewhere to pass it to our BE later and present success screen.

	- `onFail(error: NfcScannerError)` -> failing scanning process.
    <br>This is perfect time to present failure screen.
    <br>Possible error values:

      | `NFCScannerError`     | Description                     |
      |:----------------------|:----------------------------------|
      | CONNECTION_LOST       | Occurs if the device loses the connection with the document's chip |
      | NFC_NOT_ENABLED       | NFC functionality is not enabled in the device |
      | NFC_NOT_SUPPORTED     | The device does not support NFC |
      | AUTHENTICATION_FAILED | Occurs if the process of authentication fails, usually means that data provided to `NFCScannerSecurityKey` is incorrect |
      | UNKNOWN               | Unexpected error occurred. Please report this issue immediately |

#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
class NfcActivity : NfcScannerActivity() {

	// ... Activity which is going to present NFC Scanner
	companion object {
        fun start(context: Context) = with(context) {
            Intent(this, NfcActivity::class.java).let(::startActivity)
        }
    }

	// set config object providing NfcSecurityKey
	override fun getConfig(): NfcScannerConfig =
		NfcScannerConfig(
            NfcScannerSecurityKey.createWithMrtdData(DOCUMENT_NUMBER, DATE_BIRTH, EXPIRY_DATE)
        )


	// ... NFC Scanner Callback
	override fun onStepUpdate(state: NfcScannerStep) {
        // update your overlay view if needed
    }

	override fun onSuccess(result: NfcScannerResult) {
	    // show success screen
    }

    override fun onFail(error: NfcScannerError) {
  	    // show error screen
    }
}

```
</details>
<details>
  <summary>Java</summary>

```java
public class NfcActivity extends NfcScannerActivity {
    @Override
    public void onStepUpdate(@NotNull NfcScannerStep step) {
        runOnUiThread(() -> {
            // update your overlay view if needed
        });
    }

    @Override
    public void onSuccess(@NotNull NfcScannerResult result) {
        runOnUiThread(() -> {
            // show success screen
        });
    }

    @Override
    public void onFail(@NotNull NfcScannerError error) {
        runOnUiThread(() -> {
            // show error screen
        });
    }

    @NotNull
    @Override
    public NfcScannerConfig getConfig() {
        return new NfcScannerConfig(NfcScannerSecurityKey.createWithMrtdData(
                DOCUMENT_NUMBER,
                DATE_BIRTH,
                EXPIRY_DATE
        ));
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NfcActivity.class);
        context.startActivity(intent);
    }
}
```
</details>


## **Fourthline KYC**

It requires `fourthline-kyc` framework to be added to your project.

Fourthline Kyc collects all the KYC information, creates the XML and Zip it.

### KycInfo

`KycInfo` class from Fourthline SDK collect all the KYC information acting as a container and it can be modified at any time.
<br>It contains several levels of abstraction in order to provide all the KYC information.

- Instantiate `KycInfo` class

- Fill the required and optional fields that you need

- A method `validate()`  will help to validate all the fields and will return an array of `KycInfoValidationError`.

- Supported country codes can be checked at `CountryCodes.supported`
- `CountryCodes.isSupported(country)` function can be used to check if specific country code is supported by Fourthline.
- Country codes must be set as [ISO 3166-1 alpha-3](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3#Officially_assigned_code_elements) codes with the exception for Germany: `D` is considered a valid value, since that is how it is encoded in MRZ of German documents.

#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
KycInfo().apply {
// Provider
        provider = provider.copy(name = "Your Provider", clientNumber = "ClientNumber")

// Person
        person = person.copy(
            firstName = "WILLEKE LISELOTTE",
            gender = Gender.FEMALE,
            nationalityCode = "NLD",
            lastName = "DE BRUIJN",
            birthDate = Calendar.getInstance().apply { set(1965, 2, 10) }.time
        )

// Device Metadata
// Ip address is optional, location is required and should not be null
//        metadata = DeviceMetadata(ipAddress = "10.10.10.10", location = Pair(52.362208,4.8770914))
    
// Optional fields
//            .copy(
//                middleName = "MIDDLENAME",
//                birthCountryCode = "NLD",
//                birthPlace = "Amsterdam"
//            )

// Address (optional)
//        address = Address(
//            street = "Tesselschadestraat",
//            streetNumber = 12,
//            streetNumberSuffix = "A",
//            postalCode = "1054 ET",
//            city = "Amsterdam",
//            region = "North Holland",
//            countryCode = "NLD"
//        )

// Contacts
        contacts = contacts.copy(
            email = "info@fourthline.com",
            mobile = "+31123456789",
// Optional fields
//            phone = "+31123456789",
        )

// Selfie Attachment (optional)
        selfie = Selfie(image = bitmap)
// Optional fields
//            .copy(
//                timestamp = Date().time,
//                location = location,
//                videoRecording = VideoRecording(
//                    url = selfieVideoUrl,
//                    duration = VideoDuration.DEFAULT,
//                    location = location,
//                  )
//            )

// Document Attachment (auxiliary data)
        val documentFrontPage = Attachment.Document(
            image = bitmap,
            fileSide = DocumentFileSide.FRONT,
            isAngled = false
        )
// Optional fields
//            .copy(
//                timestamp = Date().time,
//                location = location
//            )

// Document (optional)
        document = Document(
            type = PASSPORT,
            images = listOf(documentFrontPage),
            number = "SPECI2014",
        )
// Optional fields
//            .copy(
//                issueDate = Calendar.getInstance().apply { set(2014, 2, 10) }.time,
//                expirationDate = Calendar.getInstance().apply { set(2024, 2, 10) }.time,
//                videoUrl = tempFile
//            )

// NFC Attachment (auxiliary data)
            .copy(
                nfc = Nfc(
                    image = bitmap,
                    mrz = "P<NLDDE<BRUIJN<<WILLEKE<LISELOTTE<<<<<<<<<<<\n" +
                            "SPECI20142NLD6503101F2403096999999990<<<<<84"
                )
// Optional fields
//                    .copy(
                          dataGroups = dataGroups,
//                        timestamp = Date().time,
//                        location = location
//                    )
            )

// Secondary Document (optional)
//        secondaryDocuments = listOf(
//              SecondaryDocument(
//                  type = DocumentType.ID_CARD,
//                  images = listOf(documentFrontPage)
//              )
// Optional fields
//            .copy(
//                issueDate = Calendar.getInstance().apply { set(2014, 2, 10) }.time,
//                expirationDate = Calendar.getInstance().apply { set(2024, 2, 10) }.time,
//            )
//        )

// Profession (optional)
// All fields are optional
//        profession = Profession(
//            employment = EMPLOYED,
//            industry = INFORMATION_COMMUNICATION,
//            profession = "Engineer"
//        )

// Tax Info (optional)
// All fields are optional
//          taxInfo = new TaxInfo(
//              taxationCountryCode = "D",
//              taxpayerIdentificationNumber = "1234ABCD",
//          )
    }

```
</details>
<details>
  <summary>Java</summary>

```java
public KycInfo kyc = new KycInfo();

// Provider
        Provider provider = kyc.getProvider();
        provider.setName("Your Provider");
        provider.setClientNumber("ClientNumber");

// Person
        Person person = kyc.getPerson();
        person.setFirstName("WILLEKE LISELOTTE");
        person.setLastName("DE BRUIJN");
        person.setBirthDate(birthDate);

// Device Metadata
// Ip address is optional, location is required and should not be null
//        DeviceMetadata metadata = new DeviceMetadata("10.10.10.10", new Pair(52.362208,4.8770914));
//        kyc.setMetadata(metadata);        
        
// Optional fields
//        person.setMiddleName("MIDDLENAME");
//        person.setGender(FEMALE);
//        person.setNationalityCode("NLD");
//        person.setBirthCountryCode("NLD");
//        person.setBirthPlace("Amsterdam");
//

// Address (optional)
// All fields are optional
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

// Document
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
//        SecondaryDocument secondaryDocument = new SecondaryDocument(ID_CARD);
//        secondaryDocument.setImages(documentPages);
// Optional fields
//        secondaryDocument.setIssueDate(issueDate);
//        secondaryDocument.setExpirationDate(expirationDate);
//        List<SecondaryDocument> secondaryDocuments = new ArrayList<>();
//        secondaryDocuments.add(secondaryDocument);

//        kyc.setSecondaryDocuments(secondaryDocuments);

// Profession (optional)
// All fields are optional
//        Profession profession = new Profession(
//                EMPLOYED,
//                "Engineer",
//                INFORMATION_COMMUNICATION
//        );
//
//        kyc.setProfession(profession);

// Tax Info (optional)
// All fields are optional
//          TaxInfo taxInfo = new TaxInfo(
//                  taxationCountryCode = "D",
//                  taxpayerIdentificationNumber = "1234ABCD",
//          );
```
</details>


Consider the following helpers that assist to fill KYC container using the scanner results:

#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
 fun attach(selfieResult: SelfieScannerResult) = with(selfieResult) {
        kyc.selfie = Attachment.Selfie(
            image = image.full,
            timestamp = metadata.timestamp.time,
            location = metadata.location,
            videoUrl = videoUrl
        )
    }

    fun attach(
        documentResult: DocumentScannerResult,
        stepResults: List<DocumentScannerStepResult>
    ) = with(documentResult) {
        // please note that type, number, issueDate and expirationDate should be set separately as part of user input
        kyc.document = Document().copy(
            videoUrl = videoUrl,
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

    fun attach(nfcResult: NfcScannerResult) = with(nfcResult) {
        kyc.document?.nfc = Attachment.Nfc(
            image = getData(type = NfcData.NfcDataType.PHOTO),
            mrz = getData<MrzInfo>(type = NfcData.NfcDataType.MRZ_INFO)?.rawMrz,
            dataGroups = getDataGroups()
                .map { it.groupNumber to it.rawData }
                .toMap(),
            timestamp = metadata.timestamp.time,
            location = metadata.location
        )
    }

```
</details>
<details>
  <summary>Java</summary>

```java
   void attach(SelfieScannerResult selfieResult) {
        Attachment.Selfie selfie = new Attachment.Selfie(selfieResult.getImage().getFull());
        selfie.setTimestamp(selfieResult.getMetadata().getTimestamp().getTime());
        selfie.setLocation(selfieResult.getMetadata().getLocation());
        selfie.setVideoUrl(selfieResult.getVideoUrl());

        kyc.setSelfie(selfie);
    }

    void attach(DocumentScannerResult documentResult, List<DocumentScannerStepResult> stepResults) {
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

    void attach(NfcScannerResult nfcResult) {
        Map<Integer, byte[]> dataGroups = new HashMap<>();
        for (NfcDataGroup group : nfcResult.getDataGroups()) {
            dataGroups.put(group.getGroupNumber(), group.getRawData());
        }

        MrzInfo mrz = nfcResult.getData(NfcData.NfcDataType.MRZ_INFO);
        String rawMrz;
        if (mrz != null) rawMrz = mrz.getRawMrz();
        else rawMrz = "";

        Attachment.Nfc nfcAttachment = new Attachment.Nfc(
                nfcResult.getData(NfcData.NfcDataType.PHOTO),
                rawMrz,
                nfcResult.getMetadata().getTimestamp().getTime(),
                nfcResult.getMetadata().getLocation(),
                dataGroups
        );
    }
```
</details>

### **Zipper**

`Zipper` class from Fourthline SDK takes `KycInfo` information to create the xml and zip it.

- Call `createZipFile()` method in order to create the zip file.
<br>⚠️ &nbsp;This method might take some time and it will block the thread on which it was called. <br>Please make sure you are not calling in on the **Main Thread**.
<br>It returns the URI where the zip file is created.

- `Zipper` throws `ZipErrors` in case of failure.
<br>Possible error values: **KycNotValid**, **CannotCreateZip**, **NotEnoughSpace**, **ZipExceedMaximumSize**.

    | `ZipperError`                   | Description                         |
    |:---------------------|:----------------------------------|
    | kycIsNotValid        | KYCInfo object is not valid, please fix validation errors first |
    | cannotCreateZip      | There was some error during zip creation |
    | zipExceedMaximumSize | Zip file exceeding max size (200 MB) |
    | notEnoughSpace       | Insufficient disk space available to create the zip file |

#### Example Usage
<details>
  <summary>Kotlin</summary>

```kotlin
try {
    Zipper().createZipFile(kyc, applicationContext)
} catch (zipperError: ZipperError) {
    when (zipperError) {
        ZipperError.KycNotValid -> "Error in kyc object"
        ZipperError.CannotCreateZip -> "Error creating zip file"
        ZipperError.NotEnoughSpace -> "There are not enough space in device"
        ZipperError.ZipExceedMaximumSize -> "Zip file exceed 200MB"
    }
}

```
</details>

## Troubleshooting

### `kotlin-reflect`

If your project uses `kotlin-reflect` it may happen that after proguard step necessary kotlin code will be removed and it will cause application to crash. In order to resolve it you need to add following section to your proguard rules:
```kotlin
-keep class com.fourthline.vision.internal.** { *; }

-keep class kotlin.** {
    public protected *;
}

-keepclassmembers @kotlin.Metadata class * {
    <methods>;
}
If you handle `onRequestPermissionsResult`, don't forget to add `super.onRequestPermissionsResult(requestCode, permissions, grantResults)`
```

