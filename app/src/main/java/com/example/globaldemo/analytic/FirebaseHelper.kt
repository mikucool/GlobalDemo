package com.example.globaldemo.analytic

import android.os.Bundle
import com.example.globaldemo.ad.constant.AdPlatform
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseHelper {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    fun initFirebase() {
        firebaseAnalytics = Firebase.analytics
    }

    // TODO: update logEvent
    fun logAdEvent(adEventInfo: AdEventInfo) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.AD_PLATFORM, adEventInfo.adPlatform.name)
            putString(FirebaseAnalytics.Param.AD_SOURCE, adEventInfo.adSource)
            putDouble(FirebaseAnalytics.Param.VALUE, adEventInfo.adRevenue)
            putString(FirebaseAnalytics.Param.CURRENCY, "USD") // Assuming USD, adjust if needed
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.AD_IMPRESSION, bundle)
    }

    // TODO: update FirebaseAdEventInfo
    data class AdEventInfo(
        val adPlatform: AdPlatform,
        val adSource: String,
        val adRevenue: Double
    )
}