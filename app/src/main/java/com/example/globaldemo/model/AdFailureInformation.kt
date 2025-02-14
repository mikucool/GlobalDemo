package com.example.globaldemo.model

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.constant.AdType

data class AdFailureInformation(
    val platform: AdPlatform,
    val adId: String,
    val adType: AdType,
)
