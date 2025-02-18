package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.constant.AdDisplayState
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.model.AdFailureInformation
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.constant.KwaiError
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAd
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdConfig
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdRequest
import com.kwai.network.sdk.loader.business.reward.interf.IKwaiRewardAdListener
import com.kwai.network.sdk.loader.common.interf.AdLoadListener

class KwaiBiddingAdController(override val adConfiguration: AdConfiguration) : BiddingAdController {

    override val videoAdsMap: MutableMap<String, AdWrapper> by lazy {
        (adConfiguration.adIdListMap[AdType.REWARD] ?: emptyList())
            .associateWith {
                AdWrapper(
                    adPlatform = adConfiguration.adPlatform,
                    adType = AdType.REWARD,
                    adId = it
                )
            }
            .toMutableMap()
    }

    override val videoAdShowCallbackMap: MutableMap<String, VideoAdShowCallback> by lazy {
        // empty mutable map
        mutableMapOf()
    }

    override fun loadAllRewardVideoAds(context: Context, eachRewardAdCallback: VideoAdLoadCallback) {
        Log.i(TAG, "loadRewardVideoAds() called with: adConfiguration = $adConfiguration")
        videoAdsMap.forEach { (adId, adWrapper) ->
            if (adWrapper.adType == AdType.REWARD && adWrapper.adInstance == null) {
                loadSpecificRewardVideoAd(context, adId, eachRewardAdCallback)
            }
        }
    }

    override fun loadSpecificRewardVideoAd(
        context: Context,
        adId: String,
        callback: VideoAdLoadCallback
    ) {
        val adWrapper = videoAdsMap[adId]
        if (adWrapper != null && adWrapper.adInstance == null && adWrapper.adType == AdType.REWARD) {
            val loader = KwaiAdSDK.getKwaiAdLoaderManager().buildRewardAdLoader(
                KwaiRewardAdConfig.Builder(
                    object : AdLoadListener<KwaiRewardAd> {
                        override fun onAdLoadStart(p0: String?) {
                            Log.d(TAG, "onAdLoadStart() called with: p0 = $p0")
                        }

                        override fun onAdLoadFailed(p0: String?, p1: KwaiError) {
                            Log.e(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                            callback.onFailedToLoad(
                                AdFailureInformation(
                                    platform = adConfiguration.adPlatform,
                                    adId = adId,
                                    adType = AdType.REWARD
                                )
                            )
                        }

                        override fun onAdLoadSuccess(p0: String?, p1: KwaiRewardAd) {
                            videoAdsMap[adId] = AdWrapper(
                                adPlatform = adConfiguration.adPlatform,
                                adType = AdType.REWARD,
                                adId = adId,
                                adRevenue = p1.price.toDoubleOrNull() ?: 0.0,
                                adInstance = p1,
                                isLoaded = true
                            )
                            Log.d(TAG, "onAdLoadSuccess() called with: ad = ${videoAdsMap[adId]}")
                            callback.onLoaded()
                        }
                    }
                ).withKwaiRewardAdListener(object : IKwaiRewardAdListener {
                    override fun onAdShow() {
                        Log.d(TAG, "onAdShow() called")
                        executeVideoAdShowCallback(adId, AdDisplayState.DISPLAYED)
                    }

                    override fun onAdShowFailed(p0: KwaiError) {
                        videoAdsMap[adId] = AdWrapper(
                            adPlatform = adConfiguration.adPlatform,
                            adType = AdType.REWARD,
                            adId = adId
                        )
                        Log.e(TAG, "onAdShowFailed() called with: p0 = $p0")
                        executeVideoAdShowCallback(adId, AdDisplayState.DISPLAY_ERROR)
                    }

                    override fun onAdClick() {
                        Log.d(TAG, "onAdClick() called")
                        executeVideoAdShowCallback(adId, AdDisplayState.CLICKED)
                    }

                    override fun onAdClose() {
                        videoAdsMap[adId] = AdWrapper(
                            adPlatform = adConfiguration.adPlatform,
                            adType = AdType.REWARD,
                            adId = adId
                        )
                        Log.d(TAG, "onAdClose() called")
                        executeVideoAdShowCallback(adId, AdDisplayState.CLOSED)
                    }

                    override fun onAdPlayComplete() {
                        Log.d(TAG, "onAdPlayComplete() called")
                    }

                    override fun onRewardEarned() {
                        Log.d(TAG, "onRewardEarned() called")
                        executeVideoAdShowCallback(adId, AdDisplayState.REWARDED)
                    }
                }).build()
            )
            loader.loadAd(KwaiRewardAdRequest(adId))
        }
    }

    override fun displayBestVideoAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback) {
        val adWrapper = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayBestVideoAd() called with: adWrapper = $adWrapper")
        when (adWrapper?.adType) {
            AdType.REWARD -> {
                if (adWrapper.adInstance != null) {
                    (adWrapper.adInstance as KwaiRewardAd).show(activity)
                }
            }

            AdType.INTERSTITIAL -> {
                if (adWrapper.adInstance != null) {
                    (adWrapper.adInstance as KwaiRewardAd).show(activity)
                }
            }

            else -> return
        }
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback) {
        val adWrapper =
            videoAdsMap.values.filter { it.adType == AdType.REWARD }.maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueRewardVideoAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) {
            (adWrapper.adInstance as KwaiRewardAd).show(activity)
        }
    }

    override fun getBestVideoAd(): AdWrapper? {
        val bestKwaiAd = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(TAG, "getBestVideoAd() called with bestKwaiAd: $bestKwaiAd")
        return bestKwaiAd
    }


    private fun executeVideoAdShowCallback(adId: String, adDisplayState: AdDisplayState) {
        Log.d(
            TAG,
            "executeVideoAdShowCallback() called with: ad = ${videoAdsMap[adId]}, adDisplayState = $adDisplayState"
        )
        when (adDisplayState) {
            AdDisplayState.DISPLAYED -> {
                videoAdShowCallbackMap[adId]?.onDisplayed()
            }

            AdDisplayState.DISPLAY_ERROR -> {
                videoAdShowCallbackMap[adId]?.onFailedToDisplay()
                videoAdShowCallbackMap.remove(adId)
            }

            AdDisplayState.CLOSED -> {
                videoAdShowCallbackMap[adId]?.onClosed()
                videoAdShowCallbackMap.remove(adId)
            }

            AdDisplayState.REWARDED -> {
                videoAdShowCallbackMap[adId]?.onRewarded()

            }

            AdDisplayState.CLICKED -> {
                videoAdShowCallbackMap[adId]?.onClicked()
            }
        }
    }

    companion object {
        private const val TAG = "KwaiBiddingAdController"
    }

}