package com.example.globaldemo.model

import android.os.Build
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.configuration.ApplicationConfiguration

data class VerificationInfo(
    val adId: String? = null,
    val androidId: String? = null,
    val androidVersion: Int = Build.VERSION.SDK_INT,
    val campaign: String? = null,
    val campaignId: String? = null,
    val channel: String? = null,
    val country: String? = null,
    val googleAdId: String? = null,
    val language: String? = "",
    val packageName: String = ApplicationConfiguration.APP_VEST_PACKAGE_NAME,
    val phoneBrand: String = Build.BRAND,
    val phoneModel: String = Build.MODEL,
    val smPkgName: String = BuildConfig.APPLICATION_ID,
    val sim: Int = -1,
    val thirdId: String? = null,
    val timeZone: String? = "",
    val userType: Int = 0,
    val uuid: String? = null,
    val vc: Int = BuildConfig.VERSION_CODE,
    val vn: String = BuildConfig.VERSION_NAME
)
