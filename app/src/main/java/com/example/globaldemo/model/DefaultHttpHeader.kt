package com.example.globaldemo.model

import android.os.Build
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.configuration.ApplicationConfiguration

data class DefaultHttpHeader(
    val adid: String? = null,
    val androidId: String? = null,
    val androidVersion: Int = Build.VERSION.SDK_INT,
    val appid: String = ApplicationConfiguration.APP_ID,
    val campaign: String? = null,
    val campaignId: String? = null,
    val channel: String? = null,
    val country: String? = null,
    val currentTime: Long = 0,
    val gaid: String? = null,
    val language: String? = "",
    val packageName: String = ApplicationConfiguration.APP_VEST_PACKAGE_NAME,
    val phoneBrand: String = Build.BRAND,
    val phoneModel: String = Build.MODEL,
    val shumengPkgName: String = BuildConfig.APPLICATION_ID,
    val sim: Int = -1,
    val thirdId: String? = null,
    val timeZone: String? = "",
    val token: String? = null,
    val userType: Int = 0,
    val uuid: String? = null,
    val vc: Int = BuildConfig.VERSION_CODE,
    val vn: String = BuildConfig.VERSION_NAME
)
