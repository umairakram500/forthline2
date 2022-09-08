# Android Fourthline SDK

* [Overview](#overview)
* [Integration](#integration)
* [Orca](#orca)
  * [Test Me](#test-me)
  * [Creating the data source](#creating-the-data-source)
  * [KYC Info](#kycinfo)
  * [Setting the KYC object](#setting-the-kycinfo-object)
  * [Default flow](#default-flow)
  * [Custom flow](#custom-flow)
  * [Standalone Selfie](#standalone-selfie)
  * [Standalone Document](#standalone-document)
* [UI Customization](#ui-customization)
  * [Fonts](#fonts)
  * [Colors](#colors)
  * [Layouts](#layout-details)
  * [Localization](#localization)
* [Zipper](#zipper)
  * [Example usage](#zipper-example-usage)
* [Fourthline Analytics](#fourthline-analytics)
  * [Enabling Fourthline Analytics](#enabling-fourthline-analytics)
  * [Analytics Tracking Consent](#analytics-tracking-consent)
  * [Example usage](#example-usage)
* [Troubleshooting](#troubleshooting)

## **Overview**
Fourthline SDK contains a drop-in module called Orca that handles all the flows and UI in order to integrate the full KYC solution in a very short period of time.

Fourthline SDK requires minimum API version 23.


## **Integration**

You can add Orca module of Fourthline SDK to your project using Gradle.

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
- Go to `Settings` -> `Account security` -> `Enable two-factor authentication`. This is required in order to access Fourthline repository.
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
    implementation "com.fourthline:fourthline-sdk:$fourthlineSdkVersion"
}
```

###### Gradle KTS
```kotlin
dependencies {
    // ...
    val fourthlineSdkVersion = "2.14.0"
    implementation("com.fourthline:fourthline-sdk:$fourthlineSdkVersion")
}
```

###### Permissions
###### Camera

`CAMERA` permission is required in order to make SDK scanners work as scanners use camera feed for detections and recording.
`RECORD_AUDIO` permission is optional, but required if the SDK scanners are configured to use record audio as well.

###### Location

SDK requires applications to have `ACCESS_FINE_LOCATION` permission in order to provide the most accurate location metadata for security checks. Starting with Android 12 the SDK allows users to proceed with `ACCESS_COARSE_LOCATION`, to comply with the guidelines set for the Android platform.

## Orca

There are a few different ways in which Orca can be integrated in the app.

### Test Me
##### Testing Orca look and feel
This is the quickest way in order to see the look and feel of Orca.
It will show the complete flow: Document scan, Selfie and Location.

```kotlin
override func onResume() {
  super.onResume()
    
  Orca.testMe(requireContext())
}
```

> Note:
> This should be used only in **debug** mode, as it does not create or return any KYC object nor does not accept any configuration

### Creating the data source

In order to display the supported countries with the corresponding document types, you need to provide the data source for the document scanner flow.
We have created an endpoint that you can call in order to get this configuration:
`https://{environment}.api.fourthline.com/v1/lists/nfcInformation`
This endpoint will return a jsonData object which then can be used to initialize the
`CountryNetworkModel`  needed by the default flow, standalone document flow and the custom flow if the document scanner is added.

> **_NOTE:_**  At this point Orca does not contain any support for networking operations. Please review our [Developer Documentation](https://dx.fourthline.com/) for more information on how to use our endpoints.

> **_NOTE:_**  This Datasource contains NFC configuration for supported documents. NFC support for Orca is coming in the near future.


```kotlin
private const val DUMMY_JSON_DATA = "[{"issuingCountry":"NLD","idDocuments":[{"type":"Passport","nationalities":["NLD"]}]}]"

private fun onSupportedCountriesAvailable(jsonData: String = DUMMY_JSON_DATA) {
    val dataSource =  object : DataSource {
        override val supportedCountries: List<CountryNetworkModel> = CountryNetworkModel.create(jsonData)
    }

    Orca.Builder(requireContext())
        .withDefaultFlow()
        .addDataSource(dataSource)
        .present(completionCallback)
}
```

### KycInfo

Fourthline KycInfo collects all the KYC information, creates the XML and zips it.

`KycInfo` class collects all the KYC information acting as a container and it can be modified at any time.
<br>It contains several levels of abstraction in order to provide all the KYC information.

- Instantiate `KycInfo` class.
- Fill in the provider.
- A method `validate()` will help to validate all the fields and will return an array of `KycInfoValidationError`.

```kotlin
fun buildKycInfo() : KycInfo {
    val provider = Provider()
    provider.clientNumber = "Client number"
    provider.name = "Provider Name"

    val kycInfo = KycInfo()
    kycInfo.provider = provider

    return kycInfo
}
```
### Setting the KycInfo object

You can inject an existing KycInfo object. Orca will create a copy and return it at the end of the flow.
If Orca is `canceled` or `failed`, it will not return any KycInfo object.
If no KycInfo object is provided Orca will create a new one, but in this case the `provider` is mandatory and needs to be filled.

```kotlin
val dataSource: DataSource
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback

Orca.Builder(requireContext())
    .withDefaultFlow()
    .addDataSource(dataSource)
    .addKycInfo(kycInfo)
    .present(completionCallback)
```

### Default Flow
- Contains Document scan, Selfie, Location

```kotlin
val dataSource: DataSource
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback

Orca.Builder(requireContext())
    .withDefaultFlow()
    // Mandatory step, initialize local data source with the country information
    .addDataSource(dataSource)
    // Add existing or newly created kyc info.
    .addKycInfo(kycInfo)
    .present(completionCallback)
```

### Custom flow
- Provides the option to customize the KYC user journey.
-  Allows adding, removing and reordering the following steps: Selfie, Document, Location

Example 1:
Creates a custom flow with Document Scanner and Location.
Notice that for the Document scanner, Orca asks for the data source after indicating that there are no more flows to be added.

```kotlin
val dataSource: DataSource
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback

Orca.Builder(requireContext())
    .withCustomFlow()
    .addDocumentFlow()
    .addLocationFlow()
    .noMoreFlows()
    .addDataSource(dataSource)
    .present(completionCallback)
```

### Standalone selfie

```kotlin
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback

Orca.Builder(requireContext())
    .withStandaloneSelfieScanner()
    .addKycInfo(kycInfo)
    .present(completionCallback)
```

### Standalone document

```kotlin
val dataSource: DataSource
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback
val documentStandaloneConfig = OrcaStandaloneDocumentConfig(documentType = DocumentType.PASSPORT)

Orca.Builder(requireContext())
    .withStandaloneDocumentScanner(documentStandaloneConfig)
    .addDataSource(dataSource)
    .addKycInfo(kycInfo)
    .present(completionCallback)
```

## UI Customization

### Fonts
Orca provides the option to customize the User Interface fonts in order to match your brand guideline.
The fonts can be changed using the `OrcaFlavor`


The table below contains a detailed description of OrcaFonts and how they map to the User Interface.

| Name | Description | Default Value |
|:--------------:|:-------------------------------------------------:|:----------------:|
| screenHeader            | The main title for each screen (e.g.“ID document”, “Select issuing country”)                                                        | Roboto-Medium 24              |
| screenTitle            | The title describing each screen (e.g. ”It’s time to scan your document”)                                                        | Roboto-Medium 18              |
| screenMessage            | The subtitle that offers an in-depth description of the screen. (e.g. “With this scan we can make sure that you really are who you say you are and avoid identity theft.”))                                                        | Roboto-Regular 16              |
| primaryButton            | Font used by the primary button. (e.g. “Continue”, “Start”)                                                        | Roboto-Medium 18             |
| secondaryButton            | Font used by the secondary button. (e.g. “< back”)                                                        | Roboto-Medium 14              |
| inputField            | Font used by the input field. (e.g. “Netherlands”)                                                        | Roboto-Medium 18              |
| inputFieldPlaceholder            | Font used by the input field placeholder. (e.g. “search…”)                                                        | Roboto-Medium 18             |
| inputFieldTitle            | Font used by the input field's title/ header. (e.g. “select issuing country”)                                                        | Roboto-Medium 14              |
| inputFieldStatus            | Font used by the input field's error/hint field. (e.g. “date must be in the future”, “ scanned from document”)                                                        | Roboto-Medium 12              |
| scannerInstructionText            | Font used by the scanner instructions. (e.g. “Please tilt your document…”)                                                        | Roboto-Medium 20              |
| confirmationScreenTitle            | Font used by the confirmation title. (e.g. “Does everything look good?”)                                                       | Roboto-Medium 20              |
| confirmationScreenCheckpoints            | Font used by the confirmation screen checkpoints. (e.g. "• Is the text readable?”)                                                        | Roboto-Regular 16            |
| tableElementTitle            | Font used for the title of the table view elements. (e.g. “National ID card”, “Passport”)                                                        | Roboto-Medium 16             |
| tableElementDescription            | Font used for the description of the table view elements. (e.g. “not accepted”)                                                        | Roboto-Regular 14             |
| instructionsLink            | Font used by the instructions button with link. (e.g. “Scan again”, “Leave identity check”)                                                        | Roboto-Medium 14             |
| hintText            | Font used for hint text usually found under the input fields. (e.g. “Please confirm the above fields.”)                                                        | Roboto-Regular 14             | 
| popupMessage            | Font used for the message text usually found in the Info popup screen. (e.g. “We need temporary access to your camera...”)                                                        | Roboto 16             |
| popupTitle            | Font used for the text usually found in the Error popup screen. (e.g. “Scan failed...”)                                                        | Roboto-Medium 24             |


```kotlin
val customFlavor = OrcaFlavor(
    fonts = OrcaFonts(
        screenHeader = OrcaFonts.Font.FromFontRes(fontRes = R.font.roboto_medium, size = 20),
        primaryButton = OrcaFonts.Font.FromFile(file = File(...), size = 18),
    )
)

Orca.Builder((requireContext()))
    .withDefaultFlow()
    .addDataSource(dataSource)
    .addFlavor(customFlavor)
    .present(completionCallback)
```

### Colors
The colors can be changed using the `OrcaFlavor`.

The table below contains a detailed description of `OrcaColors` and how they map to the User Interface.

<br>
#### Color Palette
<br>

| Name | Description | Default value Light Mode | Default value Dark Mode |
|:--------------:|:-------------------------------------------------:|:----------------:|:----------------:|
| primary            |  /// Color used for illustrations and elements that require focus such as buttons / inputs. Main color used in the app. Examples: Animations, icons, primary button background, secondary button text, second option text primary button, scanner progress, table selectors, scanner checks, links.                                                        |   #0091FF              |  #7FB3FC               | 
| accent            |  Color used for some UI elements to display choice                                                        |#00E062              | #00E062              | 
| dark            |  Color palette dark.  Mainly used for default heading color                                                        |#333333              | #FFFFFF              | 
| darkLight            |  Color palette dark light. Mainly used for default text color                                                        |#585858              | #C3CDD8              | 
| darkLighter            |  Color palette dark lighter. Mainly used for default alternative and less emphasis text color                                                        |#878787              | #3D5671              | 
| darkSoft            |  Color palette dark soft                                                        |#D6D6D6              | #263444              | 
| light            |  Color palette light. Mainly used for default background color                                                        |#FFFFFF              | #172637              | 
| gray            |  Color palette gray                                                        |#B3B3B3              | #607B93              | 

<br>

#### Orca Element

##### OrcaScreen
| Name | Description | Default value Light Mode | Default value Dark Mode |
|:--------------:|:-------------------------------------------------:|:----------------:|:----------------:|
| backgroundColor         |  Default background color                       | white                     | #172230                   |
| headerColor             |  Color used by the screen header                | color palette dark        | color palette dark        |
| titleColor              |  Color used by the screen title                 | color palette dark        | color palette dark        |
| messageColor            |  Color used by the screen message               | color palette darkLight   | color palette darkLight   |
| cellBackgroundColor     |  Cells background color                         | color palette light       | color palette light       |
| tableElementTextColor   |  Color used by the table elements cells text    | color palette darkLight   | color palette darkLight   |
| tableElementIconColor   |  Color used by the table elements cells icon    | color palette primary     | color palette primary     |


<br>

##### Buttons
| Name | Description | Default value Light Mode | Default value Dark Mode |
|:--------------------------------:|:-------------------------------------------------:|:----------------:|:----------------:|
| primary.textColor                | Primary button text color                                               | color palette light    | color palette light       |
| primary.backgroundColor          | Primary button background color                                         | color palette primary  | color palette primary     | 
| primary.borderColor              | Primary button border color                                             | black 10%              | black 10%                 | 
| secondary.textColor              | Secondary button text color                                             | color palette primary  | color palette primary     | 
| secondary.backgroundColor        | Secondary button background color                                       | background color       | background color          | 
| secondary.borderColor            | Secondary button border color                                           | black 10%              | black 10%                 | 
| backButtonColor                  | Back button text color                                                  | color palette primary  | color palette primary     | 
| scannerPrimary.textColor         | Primary scanner button text color (Yes/ Take picture)                   | color palette light    | color palette light       | 
| scannerPrimary.backgroundColor   | Primary scanner button background color                                 | color palette primary  | color palette primary     | 
| scannerPrimary.borderColor       | Primary scanner button border color                                     | black 10%              | black 10%                 | 
| scannerSecondary.textColor       | Secondary scanner button text color. Used in the Scanner confirmation screen by the "No" button          | color palette primary | white       | 
| scannerSecondary.backgroundColor | Secondary scanner button background color. Used in the Scanner confirmation screen by the "No" button    | white | color palette darkLighter   | 
| scannerSecondary.borderColor     | Secondary scanner button border color                                   | black 10%               | black 10%                | 
| linkButtonColor                  | Color used by the links                                                 | color palette primary   | color palette primary    | 

<br>

##### OrcaInputField
| Name | Description | Default value |
|:-----------------:|:-------------------------------------------------:|:----------------:|
| textColor            | Color used by the input field's text                                      | color palette dark          |
| placeholderColor     | Color used by the input field's placeholder                               | color palette dark 60%      |
| titleColor           | Color used by the input field title on the top and by the field's border  | color palette darkLight     |
| statusColor          | Color used by the input field hint message below the field                | color palette darkLighter   |
| errorColor           | Color used by the input field's error message below the field             | color palette danger        |
| backgroundColor      | Color used for the input field background                                 | background color            |
| borderColor          | Color used by the input field unfocused border                            | color palette gray          |


<br>

##### OrcaScannerConfirmation
| Name | Description | Default value Light mode| Default value Dark mode |
|:------------------:|:-------------------------------------------------:|:----------------:|:----------------:|
| textColor          | Color used to display scanner confirmation text                        | white                  | white                 | 
| backgroundColor    | Color used for the background of the scanner confirmation screens      | #333333                | #172230               | 
| bulletList         |  Color used by the bullet list                                         | color palette primary  | color palette primary |


<br>

##### OrcaHint
| Name | Description | Default value Light mode| Default value Dark mode |
|:-------------------:|:-----------------------------------------------:|:----------------:|:----------------:|
| textColor           | Color used to display hint text                 | color palette dark     | color palette dark  | 
| backgroundColor     |  Color used to for hint background              | #F3F9FF                | #3E586F             | 
| borderColor         |  Color used for the hint component border       | black 10%              | black 10%           | 

<br>

##### OrcaPopup
| Name | Description | Default value |
|:---------------------:|:------------------------------------------------:|:-----------------------------:|
| backgroundColor       | Color used for the background of the popups      | background color              |
| titleColor            | Color used by the title in the popups            | color palette dark            |
| messageColor          | Color used by the message in the popups          | color palette darkLight       |


<br>

##### OrcaGraphic
| Name | Description | Default value Light mode| Default value Dark mode|
|:---------------------:|:-------------------------------------------------:|:----------------:|:----------------:|
| backgroundColor       | Color used for the background of the intro flow animations      | #F3F3F3                | white 10%                | 
| primaryColor          | Color used by the animation in the intro screens and popups     | color palette primary  | color palette primary    |


```kotlin
val customFlavor = OrcaFlavor(
    colorsLight = OrcaColors.defaultLightColors().apply {
      buttons.primary.backgroundColor = OrcaColor.FromRes(R.color.colorPrimary)
      popup.backgroundColor = OrcaColor.FromInt(0xEEEEEE)
    }
)

Orca.Builder((requireContext()))
    .withDefaultFlow()
    .addDataSource(dataSource)
    .addFlavor(customFlavor)
    .present(completionCallback)
```


### Layout Details
Some layout details can be changed using the `OrcaFlavor`

The table below contains a detailed description of OrcaLayouts and how they map to the User Interface.

| Name | Description |
|:--------------:|:-------------------------------------------------:|
| primaryButtonCornerRadius            | Corner Radius used for the primary buttons. |

```kotlin
val customFlavor = OrcaFlavor(
    layouts = OrcaLayouts(primaryButtonCornerRadius = 8)
)

Orca.Builder((requireContext()))
    .withDefaultFlow()
    .addDataSource(dataSource)
    .addFlavor(customFlavor)
    .present(completionCallback)
```


### Localization

Orca can be configured in different languages:
- English (default)
- Dutch
- Spanish
- French
- Italian
- German
- Polish
- Romanian

`BaseLanguage` is the default language to be used in case the users' device language is not supported and is not defined in LanguageType. Default is set to English.
The base language can be overridden and changed by updating the `flavor.localisation.fixedLanguage` property.


```kotlin
val dataSource: DataSource
val kycInfo: KycInfo
val completionCallback: OrcaCompletionCallback

val customFlavor = OrcaFlavor(
    localisation = OrcaLocalisation(fixedLanguage = OrcaLocalisation.LanguageType.ES)
)

Orca.Builder((requireContext()))
    .withDefaultFlow()
    .addDataSource(dataSource)
    .addFlavor(customFlavor)
    .addKycInfo(kycInfo)
    .present(completionCallback)
```

## Zipper
`Zipper` class takes `KYCInfo` information to create the xml and zip it together with attached images and videos.

- Call `createZipFile()` method in order to create the zip file.
  <br>It returns the URI of the created zip file or throws `ZipperError` in case of failure.
  <br>⚠️ &nbsp;This method might take some time and it will block the thread on which it was called.<br>Please make sure you are not calling in on the **Main Thread**.

  <br>Possible error values:

  | `ZipperError`                  | Description                       |
  |:-------------------------------|:----------------------------------|
  | KycNotValid                    | KYCInfo object is not valid, please fix validation errors first, see `kycInfo.validate()` for the errors. |
  | CannotCreateZip                | There was some error during zip creation |
  | ZipExceedMaximumSize           | zip file exceeding max size (200 MB) |
  | NotEnoughSpace                 | Insufficient disk space available to create the zip file |

### Zipper Example Usage
```kotlin
val kycInfo: KycInfo
val zipResult: URI = Zipper().createZipFile(kycInfo, requireContext())
```

## Fourthline Analytics

It requires the [Datadog](https://github.com/DataDog/dd-sdk-android) dependency to be available at runtime.

### Notice
* **The Datadog SDK does not support initializing and running multiple instances.**
* **If you are already using the Datadog Android SDK in your project, you are not able to make use of Fourthline Analytics.**
* **After initializing Fourthline Analytics, please do not attempt to initialize or make use of your own custom Datadog implementation.**


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
} catch (AnalyticsError error) {
    // handle exception
}

FourthlineAnalytics.setTrackingConsent(TrackingConsent.GRANTED);
```
</details>


## Troubleshooting

### `kotlin-reflect`

If your project uses `kotlin-reflect` it may happen that after proguard step necessary kotlin code will be removed and it will cause application to crash. In order to resolve it you need to add following section to your proguard rules:
```kotlin
-keep class com.fourthline.vision.internal.** { *; }

-keep class kotlin.coroutines.Continuation {
  public protected *;
}

-keepclassmembers @kotlin.Metadata class com.fourthline.vision.internal.** {
  <methods>;
}
```