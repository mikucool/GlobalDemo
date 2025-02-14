package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.callback.InterstitialAdCallback
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
    override fun loadInterstitialAds(context: Context, callback: InterstitialAdCallback) {}

    private val rewardAdsMap: MutableMap<String, RewardVideoAd?> by lazy {
        (adConfiguration.adIdListMap[AdType.REWARD] ?: emptyList())
            .associateWith { null }
            .toMutableMap()
    }

    override fun loadAllRewardVideoAds(context: Context, eachRewardAdCallback: RewardAdCallback) {
        Log.i(
            TAG,
            "loadRewardVideoAds() called with: callback = $eachRewardAdCallback, adConfiguration = $adConfiguration"
        )
        rewardAdsMap.forEach { (adId, rewardAd) ->
            if (rewardAd == null) loadSpecificRewardVideoAd(context, adId, eachRewardAdCallback)
        }
    }

    override fun loadSpecificRewardVideoAd(
        context: Context,
        adId: String,
        callback: RewardAdCallback
    ) {
        val specificRewardAd = rewardAdsMap[adId]
        if (specificRewardAd == null) {
            val request = RewardVideoAdRequest.Builder()
                .withSlotId(adId)
                .build()
            val loader = RewardVideoAdLoader.Builder()
                .withAdLoadListener(object : AdLoadListener<RewardVideoAd> {
                    override fun onError(p0: AdError) {
                        Log.d(TAG, "onError() called with: p0 = ${p0.message}")
                        callback.onFailedToLoad(
                            AdFailureInformation(
                                platform = adConfiguration.adPlatform,
                                adId = adId,
                                adType = AdType.REWARD
                            )
                        )
                    }

                    override fun onAdLoaded(p0: RewardVideoAd) {
                        rewardAdsMap[adId] = p0
                        Log.d(TAG, "onAdLoaded() called with: p0 = ${p0.bid?.price}")
                        specificRewardAd?.setAdInteractionListener(object :
                            RewardAdInteractionListener {
                            override fun onAdError(p0: AdError) {
                                Log.d(TAG, "onAdError() called with: p0 = $p0")
                                rewardAdsMap[adId] = null
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
                                rewardAdsMap[adId] = null
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
        val rewardAd = rewardAdsMap.values.filterNotNull().maxByOrNull { it.bid?.price ?: 0.0 }
        rewardAd?.show(activity)
    }

    override fun getHighestRewardAdRevenue(): Double {
        Log.i(
            TAG,
            "getHighestRewardAdRevenue() called with ad platform: ${adConfiguration.adPlatform}, revenueList: ${rewardAdsMap.values.map { it?.bid?.price }}"
        )
        return rewardAdsMap.values.filterNotNull().maxOfOrNull { it.bid?.price ?: 0.0 } ?: 0.0
    }

    companion object {
        private const val TAG = "BigoBiddingAdController"
    }
}