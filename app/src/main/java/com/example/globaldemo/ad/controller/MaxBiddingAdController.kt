package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.constant.AdDisplayState
import com.example.globaldemo.ad.controller.KwaiBiddingAdController.Companion
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.model.AdFailureInformation

class MaxBiddingAdController(override val adConfiguration: AdConfiguration) : BiddingAdController {
    override val videoAdsMap: MutableMap<String, AdWrapper> by lazy {
        val rewardAdMap = (adConfiguration.adIdListMap[AdType.REWARD] ?: emptyList())
            .associateWith {
                AdWrapper(
                    adPlatform = adConfiguration.adPlatform,
                    adType = AdType.REWARD,
                    adId = it,
                )
            }
            .toMutableMap()
        val interstitialAdMap = (adConfiguration.adIdListMap[AdType.INTERSTITIAL] ?: emptyList())
            .associateWith {
                AdWrapper(
                    adPlatform = adConfiguration.adPlatform,
                    adType = AdType.INTERSTITIAL,
                    adId = it,
                )
            }
            .toMutableMap()
        // Merge the two maps
        (rewardAdMap + interstitialAdMap).toMutableMap()
    }

    override val videoAdShowCallbackMap: MutableMap<String, VideoAdShowCallback> by lazy {
        // empty mutable map
        mutableMapOf()
    }

    override fun loadAllInterstitialAds(
        context: Context,
        eachInterstitialAdCallback: VideoAdLoadCallback
    ) {
        Log.i(TAG, "loadAllInterstitialAds() called with: configuration = $adConfiguration")
        videoAdsMap.forEach { (adId, adWrapper) ->
            if (adWrapper.adType == AdType.INTERSTITIAL && adWrapper.adInstance == null) {
                loadSpecificInterstitialAd(context, adId, eachInterstitialAdCallback)
            }
        }
    }

    override fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: VideoAdLoadCallback
    ) {
        Log.i(TAG, "loadAllRewardVideoAds() called with: configuration = $adConfiguration")
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
            val maxRewardAd = MaxRewardedAd.getInstance(adId, context)
            maxRewardAd.setListener(object : MaxRewardedAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.REWARD,
                        adId = adId,
                        adRevenue = p0.revenue,
                        adInstance = p0,
                        isLoaded = true
                    )
                    Log.d(TAG, "onAdLoaded() called with: ad = ${videoAdsMap[adId]}")
                    callback.onLoaded()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.e(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                    callback.onFailedToLoad(
                        AdFailureInformation(
                            platform = adConfiguration.adPlatform,
                            adId = adId,
                            adType = AdType.REWARD
                        )
                    )
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d(TAG, "onAdDisplayed() called with: p0 = $p0")
                    executeVideoAdShowCallback(adId, AdDisplayState.DISPLAYED)
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d(TAG, "onAdHidden() called with: p0 = $p0")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.REWARD,
                        adId = adId
                    )
                    executeVideoAdShowCallback(adId, AdDisplayState.CLOSED)
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d(TAG, "onAdClicked() called with: p0 = $p0")
                    executeVideoAdShowCallback(adId, AdDisplayState.CLICKED)
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.e(TAG, "onAdDisplayFailed() called with: p0 = $p0, p1 = $p1")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.REWARD,
                        adId = adId
                    )
                    executeVideoAdShowCallback(adId, AdDisplayState.DISPLAY_ERROR)
                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    Log.d(TAG, "onUserRewarded() called with: p0 = $p0, p1 = $p1")
                    executeVideoAdShowCallback(adId, AdDisplayState.REWARDED)
                }
            })
            maxRewardAd.loadAd()
        }
    }

    override fun loadSpecificInterstitialAd(
        context: Context,
        adId: String,
        callback: VideoAdLoadCallback
    ) {
        val adWrapper = videoAdsMap[adId]
        if (adWrapper != null && adWrapper.adInstance == null && adWrapper.adType == AdType.INTERSTITIAL) {
            val ad = MaxInterstitialAd(adId, context)
            ad.setListener(object : MaxAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.INTERSTITIAL,
                        adId = adId,
                        adInstance = ad,
                        isLoaded = true
                    )
                    Log.d(TAG, "onAdLoaded() called with: ad = ${videoAdsMap[adId]}")
                    callback.onLoaded()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.e(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                    callback.onFailedToLoad(
                        AdFailureInformation(
                            platform = adConfiguration.adPlatform,
                            adId = adId,
                            adType = AdType.INTERSTITIAL
                        )
                    )
                }

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d(TAG, "onAdDisplayed() called with: p0 = $p0")
                    executeVideoAdShowCallback(adId, AdDisplayState.DISPLAYED)
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d(TAG, "onAdHidden() called with: p0 = $p0")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.INTERSTITIAL,
                        adId = adId
                    )
                    executeVideoAdShowCallback(adId, AdDisplayState.CLOSED)
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d(TAG, "onAdClicked() called with: p0 = $p0")
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.e(TAG, "onAdDisplayFailed() called with: p0 = $p0, p1 = $p1")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.INTERSTITIAL,
                        adId = adId
                    )
                    executeVideoAdShowCallback(adId, AdDisplayState.DISPLAY_ERROR)
                }
            })
            ad.loadAd()
        }
    }

    override fun displayBestVideoAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback) {
        val adWrapper = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(TAG, "displayBestVideoAd() called with: adWrapper = $adWrapper")
        when (adWrapper?.adType) {
            AdType.REWARD -> {
                if (adWrapper.adInstance != null) {
                    (adWrapper.adInstance as MaxRewardedAd).showAd(activity)
                }
            }

            AdType.INTERSTITIAL -> {
                if (adWrapper.adInstance != null) {
                    (adWrapper.adInstance as MaxInterstitialAd).showAd(activity)
                }
            }

            else -> return
        }

    }

    override fun displayHighestRevenueInterstitialAd(activity: Activity) {
        val adWrapper = videoAdsMap.values.filter { it.adType == AdType.INTERSTITIAL }.toList()
            .maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueInterstitialAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) {
            (adWrapper.adInstance as MaxInterstitialAd).showAd(activity)
        }
    }


    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val adWrapper = videoAdsMap.values.filter { it.adType == AdType.REWARD }.toList()
            .maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueRewardVideoAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) {
            (adWrapper.adInstance as MaxRewardedAd).showAd(activity)
        }
    }

    override fun getBestVideoAd(): AdWrapper? {
        val bestMaxAd = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(TAG, "getHighestRewardAdRevenue() called with bestKwaiAd: $bestMaxAd")
        return bestMaxAd
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
        const val TAG = "MaxBiddingAdController"
    }

}