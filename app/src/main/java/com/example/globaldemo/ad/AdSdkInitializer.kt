package com.example.globaldemo.ad

import android.content.Context
import android.util.Log
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkInitializationConfiguration
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.google.android.gms.ads.MobileAds
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.api.KwaiInitCallback
import com.kwai.network.sdk.api.SdkConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sg.bigo.ads.BigoAdSdk
import sg.bigo.ads.api.AdConfig

object AdSdkInitializer {
    private const val TAG = "AdSdkInitializer"

    fun initAdSdk(context: Context, platform: AdPlatform, onInitialized: (() -> Unit)? = null) {
        when (platform) {
            AdPlatform.KWAI -> initKwaiAd(context, onInitialized)
            AdPlatform.BIGO -> initBigoAd(context, onInitialized)
            AdPlatform.MAX -> initMaxAd(context, onInitialized)
            AdPlatform.ADMOB -> initAdMob(context, onInitialized)
            else -> {}
        }
    }

    private fun initKwaiAd(context: Context, onInitialized: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            KwaiAdSDK.init(
                context, SdkConfig.Builder()
                    .appId(ApplicationConfiguration.AD_KWAI_APP_ID)
                    .token(ApplicationConfiguration.AD_KWAI_TOKEN)
                    .debug(true)
                    .setInitCallback(object : KwaiInitCallback {
                        override fun onSuccess() {
                            Log.d(TAG, "initKwaiAd onSuccess() called")
                            // invoke callback on main thread
                            CoroutineScope(Dispatchers.Main).launch {
                                onInitialized?.invoke()
                            }
                        }

                        override fun onFail(p0: Int, p1: String?) {
                            Log.d(TAG, "initKwaiAd onFail() called with: p0 = $p0, p1 = $p1")
                        }
                    })
                    .build()
            )
        }
    }

    private fun initBigoAd(context: Context, onInitialized: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val config = AdConfig.Builder()
                .setAppId(ApplicationConfiguration.AD_BIGO_APP_ID)
                .setDebug(true)
                .build()
            BigoAdSdk.initialize(context, config) {
                Log.d(TAG, "initBigoAd() called with initialized")
                CoroutineScope(Dispatchers.Main).launch {
                    onInitialized?.invoke()
                }
            }
        }
    }

    private fun initMaxAd(context: Context, onInitialized: (() -> Unit)? = null) {
        // Create the initialization configuration
        CoroutineScope(Dispatchers.IO).launch {
            val initConfig = AppLovinSdkInitializationConfiguration.builder(
                ApplicationConfiguration.AD_MAX_KEY,
                context
            )
                .setMediationProvider(AppLovinMediationProvider.MAX)
                // Perform any additional configuration/setting changes
                .build()
            AppLovinSdk.getInstance(context).initialize(initConfig) { sdkConfig ->
                Log.d(TAG, "initMaxAd() called with: sdkConfig = $sdkConfig")
                updateMaxAdUserSettings(context)
                CoroutineScope(Dispatchers.Main).launch {
                    onInitialized?.invoke()
                }
            }
        }
    }

    private fun initAdMob(context: Context, onInitialized: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(context) { status ->
                Log.d(TAG, "initAdMob() called with: init status = ${status.adapterStatusMap}")
                CoroutineScope(Dispatchers.Main).launch {
                    onInitialized?.invoke()
                }
            }
        }
    }

    private fun updateMaxAdUserSettings(context: Context) {
        val settings = AppLovinSdk.getInstance(context).settings
        settings.userIdentifier = "this is a test user identifier"
    }
}