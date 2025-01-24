package com.example.globaldemo.model

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.constant.AdType

/**
 * @param adPlatform Ad平台
 * @param adIdListMap Ad平台对应的广告位ID列表, key: AdType, value: List<String>
 */
data class AdConfiguration(
    val adPlatform: AdPlatform = AdPlatform.NONE,
    val adIdListMap: Map<AdType, List<String>> = emptyMap()
)
