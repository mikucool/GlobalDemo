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
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.callback.RewardAdCallback
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

    override fun loadAllInterstitialAds(context: Context, eachInterstitialAdCallback: InterstitialAdCallback) {
        Log.i(TAG, "loadInterstitialAds with videoAdsMap: $videoAdsMap")
        videoAdsMap.forEach { (adId, adWrapper) ->
            if (adWrapper.adType == AdType.INTERSTITIAL && adWrapper.adInstance == null) {
                loadSpecificInterstitialAd(context, adId, eachInterstitialAdCallback)
            }
        }
    }

    override fun loadAllRewardVideoAds(context: Context, eachRewardAdCallback: RewardAdCallback) {
        videoAdsMap.forEach { (adId, adWrapper) ->
            if (adWrapper.adType == AdType.REWARD && adWrapper.adInstance == null) {
                loadSpecificRewardVideoAd(context, adId, eachRewardAdCallback)
            }
        }
    }

    override fun loadSpecificRewardVideoAd(
        context: Context,
        adId: String,
        callback: RewardAdCallback
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

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d(TAG, "onAdDisplayed() called with: p0 = $p0")
                    callback.onDisplayed()
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d(TAG, "onAdHidden() called with: p0 = $p0")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.REWARD,
                        adId = adId
                    )
                    callback.onClosed()
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d(TAG, "onAdClicked() called with: p0 = $p0")
                    callback.onClicked()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.d(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                    callback.onFailedToLoad(
                        AdFailureInformation(
                            platform = adConfiguration.adPlatform,
                            adId = adId,
                            adType = AdType.REWARD
                        )
                    )
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.d(TAG, "onAdDisplayFailed() called with: p0 = $p0, p1 = $p1")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.REWARD,
                        adId = adId
                    )
                    callback.onFailedToDisplay()
                }

                override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                    Log.d(TAG, "onUserRewarded() called with: p0 = $p0, p1 = $p1")
                    callback.onRewarded()
                }
            })
            maxRewardAd.loadAd()
        }
    }

    override fun loadSpecificInterstitialAd(
        context: Context,
        adId: String,
        callback: InterstitialAdCallback
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

                override fun onAdDisplayed(p0: MaxAd) {
                    Log.d(TAG, "onAdDisplayed() called with: p0 = $p0")
                    callback.onDisplayed()
                }

                override fun onAdHidden(p0: MaxAd) {
                    Log.d(TAG, "onAdHidden() called with: p0 = $p0")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.INTERSTITIAL,
                        adId = adId
                    )
                    callback.onClosed()
                }

                override fun onAdClicked(p0: MaxAd) {
                    Log.d(TAG, "onAdClicked() called with: p0 = $p0")
                    callback.onClicked()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.d(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                    callback.onFailedToLoad()
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    Log.d(TAG, "onAdDisplayFailed() called with: p0 = $p0, p1 = $p1")
                    videoAdsMap[adId] = AdWrapper(
                        adPlatform = adConfiguration.adPlatform,
                        adType = AdType.INTERSTITIAL,
                        adId = adId
                    )
                    callback.onFailedToDisplay()
                }
            })
            ad.loadAd()
        }
    }

    override fun displayHighestRevenueInterstitialAd(activity: Activity) {
    }


    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val adWrapper = videoAdsMap.values.toList().maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueRewardVideoAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) (adWrapper.adInstance as MaxRewardedAd).showAd(activity)
    }

    override fun getBestAd(): AdWrapper? {
        val bestMaxAd = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(KwaiBiddingAdController.TAG, "getHighestRewardAdRevenue() called with bestKwaiAd: $bestMaxAd")
        return bestMaxAd
    }

    companion object {
        const val TAG = "MaxBiddingAdController"
    }

}