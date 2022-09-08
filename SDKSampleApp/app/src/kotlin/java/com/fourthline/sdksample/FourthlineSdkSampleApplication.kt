/**
 * Copyright © 2021 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.app.Application
import android.util.Log
import com.fourthline.analytics.AnalyticsError
import com.fourthline.analytics.FourthlineAnalytics
import com.fourthline.analytics.TrackingConsent

class FourthlineSdkSampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        configureFourthlineSdkAnalytics()
    }

    /**
     * Configure the FourthlineAnalytics library for your given TENANT_ID.
     *
     * After initialisation, analytics data is collected locally, but not sent yet.
     * Set the 'trackingConsent' to [TrackingConsent.GRANTED] to start sending analytics data.
     *
     * Consider compliance with privacy laws for your area of operations before setting
     * TrackingConsent.
     */
    private fun configureFourthlineSdkAnalytics() {
        try {
            FourthlineAnalytics.initialize("TENANT_ID", this)
        } catch (analyticsError: AnalyticsError) {
            Log.e(
                "FourthlineSdkSampleApplication", "Analytics cannot be initialised",
                analyticsError
            )
        }

        FourthlineAnalytics.setTrackingConsent(TrackingConsent.GRANTED)
    }
}