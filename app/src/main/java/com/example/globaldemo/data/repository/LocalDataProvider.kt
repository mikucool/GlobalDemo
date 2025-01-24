package com.example.globaldemo.data.repository

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.model.AdConfiguration

object LocalDataProvider {
    fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration {
        return AD_CONFIGURATIONS.find { it.adPlatform == adPlatform } ?: AdConfiguration()
    }

    private val AD_CONFIGURATIONS = listOf(
        // max
        AdConfiguration(
            adPlatform = AdPlatform.MAX,
            adIdListMap = mapOf(
                AdType.REWARD to listOf(
                    ApplicationConfiguration.AD_MAX_REWARD_ID_1,
                    ApplicationConfiguration.AD_MAX_REWARD_ID_2,
                ),
                AdType.INTERSTITIAL to listOf(
                    ApplicationConfiguration.AD_MAX_INTERSTITIAL_ID_1,
                    ApplicationConfiguration.AD_MAX_INTERSTITIAL_ID_2,
                ),
                AdType.BANNER to listOf(),
                AdType.NATIVE to listOf()
            )
        ),
        // bigo
        AdConfiguration(
            adPlatform = AdPlatform.BIGO,
            adIdListMap = mapOf(
                AdType.REWARD to listOf(
                    ApplicationConfiguration.AD_BIGO_REWARD_ID_1,
                    ApplicationConfiguration.AD_BIGO_REWARD_ID_2,
                ),
                AdType.INTERSTITIAL to listOf(),
                AdType.BANNER to listOf(),
                AdType.NATIVE to listOf()
            )
        ),
        // kwai
        AdConfiguration(
            adPlatform = AdPlatform.KWAI,
            adIdListMap = mapOf(
                AdType.REWARD to listOf(
                    ApplicationConfiguration.AD_KWAI_REWARD_ID_1,
                    ApplicationConfiguration.AD_KWAI_REWARD_ID_2,
                    ApplicationConfiguration.AD_KWAI_REWARD_ID_3,
                ),
                AdType.INTERSTITIAL to listOf(),
                AdType.BANNER to listOf(),
                AdType.NATIVE to listOf()
            )
        ),
    )
}