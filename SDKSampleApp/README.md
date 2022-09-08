# Android Fourthline Sample app

## **OVERVIEW**
Sample app provides examples on how Fourthline SDK can be integrated into your own app.
Main screen contains four buttons: Selfie, Document, NFC, KYC ZIP, each launches corresponding process.
In the end of the process the results are presented on separate result screen or, in case of critical error occurs during process, you'll be redirected to main screen with popup explaining failure reason.

## **INTEGRATION**

- Add access token to be able to access the Fourthline SDK to the end of the `local.properties` file as following: `FourthlineSdkGithubToken=YOUR_ACCESS_TOKEN_HERE`
- Sync project with gradle files.
- Pick one of the build variants: `kotlinDebug` or `javaDebug`
- Build and run the app.