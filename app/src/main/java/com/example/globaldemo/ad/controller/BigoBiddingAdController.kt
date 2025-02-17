package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.model.AdFailureInformation
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdLoadListener
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

    override fun loadAllRewardVideoAds(context: Context, eachRewardAdCallback: RewardAdCallback) {
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
        callback: RewardAdCallback
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
                                callback.onFailedToDisplay()
                            }

                            override fun onAdImpression() {
                                Log.d(TAG, "onAdImpression() called")
                                callback.onDisplayed()
                            }

                            override fun onAdClicked() {
                                Log.d(TAG, "onAdClicked() called")
                                callback.onClicked()
                            }

                            override fun onAdOpened() {
                                Log.d(TAG, "onAdOpened() called")
                                callback.onOpened()
                            }

                            override fun onAdClosed() {
                                Log.d(TAG, "onAdClosed() called")
                                videoAdsMap[adId] = AdWrapper(
                                    adPlatform = adConfiguration.adPlatform,
                                    adType = AdType.REWARD,
                                    adId = adId
                                )
                                callback.onClosed()
                            }

                            override fun onAdRewarded() {
                                Log.d(TAG, "onAdRewarded() called")
                                callback.onRewarded()
                            }
                        })
                        callback.onLoaded()
                    }
                })
                .build()
            loader.loadAd(request)
        }
    }

    override fun displayHighestRevenueInterstitialAd(activity: Activity) {
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val adWrapper = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.d(TAG, "displayHighestRevenueRewardVideoAd() called with: adWrapper = $adWrapper")
        if (adWrapper?.adInstance != null) (adWrapper.adInstance as RewardVideoAd).show(activity)
    }

    override fun getBestAd(): AdWrapper? {
        val bestBigoAd = videoAdsMap.values.maxByOrNull { it.adRevenue }
        Log.i(TAG, "getHighestRewardAdRevenue() called with bestBigoAd: $bestBigoAd")
        return bestBigoAd
    }

    companion object {
        private const val TAG = "BigoBiddingAdController"
    }
}