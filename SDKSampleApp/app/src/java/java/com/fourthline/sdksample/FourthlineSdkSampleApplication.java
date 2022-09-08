package com.fourthline.sdksample;

import android.app.Application;
import android.util.Log;

import com.fourthline.analytics.AnalyticsError;
import com.fourthline.analytics.FourthlineAnalytics;
import com.fourthline.analytics.TrackingConsent;

public class FourthlineSdkSampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FourthlineAnalytics.initialize("TENANT_ID", this);
        } catch (AnalyticsError analyticsError) {
            Log.e(
                    "FourthlineSdkSampleApplication", "Analytics cannot be initialised",
                    analyticsError
            );
        }

        FourthlineAnalytics.setTrackingConsent(TrackingConsent.GRANTED);
    }
}
