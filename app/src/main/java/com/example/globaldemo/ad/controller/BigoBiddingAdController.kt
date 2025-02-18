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
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdLoadListener
import sg.bigo.ads.api.InterstitialAd
import sg.bigo.ads.api.RewardAdInteractionListener
import sg.bigo.ads.api.RewardVideoAd
import sg.bigo.ads.api.RewardVideoAdLoader
import sg.bigo.ads.api.RewardVideoAdRequest

class BigoBiddingAdController(override val adConfiguration: AdConfiguration) : BiddingAdController {
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

    override fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: VideoAdLoadCallback
    ) {
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
            val request = RewardVideoAdRequest.Builder()
                .withSlotId(adId)
                .build()
            val loader = RewardVideoAdLoader.Builder()
                .withAdLoadListener(object : AdLoadListener<RewardVideoAd> {
                    override fun onError(p0: AdError) {
                        Log.e(TAG, "onError() called with: p0 = ${p0.message}")
                        callback.onFailedToLoad(
                            AdFailureInformation(
                                platform = adConfiguration.adPlatform,
                                adId = adId,
                                adType = AdType.REWARD
                            )
                        )
                    }

                    override fun onAdLoaded(p0: RewardVideoAd) {
                        videoAdsMap[adId] = AdWrapper(
                            adPlatform = adConfiguration.adPlatform,
                            adType = AdType.REWARD,
                            adId = adId,
                            adRevenue = p0.bid?.price ?: 0.0,
                            adInstance = p0,
                            isLoaded = true
                        )
                        Log.i(TAG, "onAdLoaded() called with: ad = ${videoAdsMap[adId]}")
                        p0.setAdInteractionListener(object :
                            RewardAdInteractionListener {
                            override fun onAdError(p0: AdError) {
                                Log.e(TAG, "onAdError() called with: p0 = $p0")
                                videoAdsMap[adId] = AdWrapper(
                                    adPlatform = adConfiguration.adPlatform,
                                    adType = AdType.REWARD,
                                    adId = adId
                                )
                                executeVideoAdShowCallback(adId, AdDisplayState.DISPLAY_ERROR)
                            }

                            override fun onAdImpression() {
                                Log.d(TAG, "onAdImpression() called")
                                executeVideoAdShowCallback(adId, AdDisplayState.DISPLAYED)
                            }

                            override fun onAdClicked() {
                                Log.d(TAG, "onAdClicked() called")
                                executeVideoAdShowCallback(adId, AdDisplayState.CLICKED)
                            }

                            override fun onAdOpened() {
                                Log.d(TAG, "onAdOpened() called")
                            }

                            override fun onAdClosed() {
                                Log.d(TAG, "onAdClosed() called")
                                videoAdsMap[adId] = AdWrapper(
                                    adPlatform = adConfiguration.adPlatform,
                                    adType = AdType.REWARD,
                                    adId = adId
                                )
                                executeVideoAdShowCallback(adId, AdDisplayState.CLOSED)
                            }

                            override fun onAdRewarded() {
                                Log.d(TAG, "onAdRewarded() called")
                                executeVideoAdShowCallback(adId, AdDisplayState.REWARDED)
                            }
                        })
                        callback.onLoaded()
                    }
                })
                .build()
            loader.loadAd(request)
        }
    }

    override fun displayBestVideoAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback) {
        val adWrapper = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayBestVideoAd() called with: adWrapper = $adWrapper")
        when (adWrapper?.adType) {
            AdType.REWARD -> {
                if (adWrapper.adInstance != null) {
                    videoAdShowCallbackMap[adWrapper.adId] = videoAdShowCallback
                    (adWrapper.adInstance as RewardVideoAd).show(activity)
                }
            }

            AdType.INTERSTITIAL -> {
                if (adWrapper.adInstance != null) {
                    videoAdShowCallbackMap[adWrapper.adId] = videoAdShowCallback
                    (adWrapper.adInstance as InterstitialAd).show(activity)
                }
            }

            else -> return
        }
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val adWrapper =
            videoAdsMap.values.filter { it.adType == AdType.REWARD }.maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueRewardVideoAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) (adWrapper.adInstance as RewardVideoAd).show(activity)
    }

    override fun getBestVideoAd(): AdWrapper? {
        val bestBigoAd = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(TAG, "getHighestRewardAdRevenue() called with bestBigoAd: $bestBigoAd")
        return bestBigoAd
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
        private const val TAG = "BigoBiddingAdController"
    }
}