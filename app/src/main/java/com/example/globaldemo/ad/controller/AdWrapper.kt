package com.example.globaldemo.ad.controller

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.constant.AdType

data class AdWrapper(
    val adPlatform: AdPlatform = AdPlatform.NONE,
    val adType: AdType = AdType.UNKNOWN,
    val adId: String = "",
    val adRevenue: Double = 0.0,
    val adInstance: Any? = null,
    val isLoaded: Boolean = false
)
