package com.example.globaldemo.ad

import android.content.Context
import android.util.Log
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.api.KwaiInitCallback
import com.kwai.network.sdk.api.SdkConfig
import sg.bigo.ads.BigoAdSdk
import sg.bigo.ads.api.AdConfig

class AdUseCase {
    companion object {
        const val TAG = "AdUseCase"
        fun initKwaiAd(context: Context) {
            KwaiAdSDK.init(
                context, SdkConfig.Builder()
                    .appId(ApplicationConfiguration.AD_KWAI_APP_ID)
                    .token(ApplicationConfiguration.AD_KWAI_TOKEN)
                    .debug(true)
                    .setInitCallback(object : KwaiInitCallback {
                        override fun onSuccess() {
                            Log.d(TAG, "initKwaiAd onSuccess() called")
                        }

                        override fun onFail(p0: Int, p1: String?) {
                            Log.d(TAG, "initKwaiAd onFail() called with: p0 = $p0, p1 = $p1")
                        }
                    })
                    .build()
            )
        }

        fun initBigoAd(context: Context) {
            val config = AdConfig.Builder()
                .setAppId(ApplicationConfiguration.AD_BIGO_APP_ID)
                .setDebug(true)
                .build()
            BigoAdSdk.initialize(
                context, config
            ) {
                Log.d(TAG, "initBigoAd() called with initialized")
            }
        }
    }
}