package com.example.globaldemo.verification

import android.annotation.SuppressLint
import android.provider.Settings
import com.example.globaldemo.GlobalDemoApplication
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import java.util.Locale
import java.util.UUID

object VerificationHelper {

    private var androidId: String? = null

    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        return if (androidId == null || androidId!!.isEmpty()) Settings.Secure.getString(
            GlobalDemoApplication.instance.contentResolver,
            Settings.Secure.ANDROID_ID
        ) else androidId!!
    }

    private var googleAdId: String? = null
    fun getGoogleAdId(): String? {
        if (googleAdId != null) return googleAdId
        var adInfo: AdvertisingIdClient.Info? = null
        try {
            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(GlobalDemoApplication.instance)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        googleAdId = adInfo?.id
        return googleAdId
    }

    fun getTimeZone(): String? {
        return try {
            java.util.TimeZone.getDefault().id
        } catch (e: Exception) {
            null
        }
    }

    fun getLanguage(): String? {
        return GlobalDemoApplication.instance.resources?.configuration?.locales?.get(0)?.language
    }

    fun getSim(): Int {
        return 0
    }

    fun getCountry(): String {
        return try {
            var country = GlobalDemoApplication.instance.resources.configuration.locales[0].country
            if (country.isEmpty()) {
                country = Locale.getDefault().country
            }
            country
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    fun getUUID(): String {
        return "mock UUID"
    }

    fun getUerType(): Int = 0

    fun getRandomToken(): String = UUID.randomUUID().toString()

    fun getAdId(): String {
        return "AD_ID"
    }

    fun getChannel(): String {
        return "CHANNEL"
    }

    fun getCampaign(): String {
        return "CAMPAIGN"
    }

    fun getCampaignId(): String {
        return "CAMPAIGN_ID"
    }

    fun getThirdId(): String {
        return "THIRD_ID"
    }

}