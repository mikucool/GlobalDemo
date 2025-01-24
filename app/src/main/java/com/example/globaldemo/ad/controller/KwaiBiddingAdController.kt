package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.model.AdConfiguration
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.constant.KwaiError
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAd
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdConfig
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdRequest
import com.kwai.network.sdk.loader.business.reward.interf.IKwaiRewardAdListener
import com.kwai.network.sdk.loader.common.interf.AdLoadListener

class KwaiBiddingAdController(override val adConfiguration: AdConfiguration) : BiddingAdController {

    private val rewardAdsMap: MutableMap<String, KwaiRewardAd?> by lazy {
        (adConfiguration.adIdListMap[AdType.REWARD] ?: emptyList())
            .associateWith { null }
            .toMutableMap()
    }

    override fun loadInterstitialAds() {

    }

    override fun loadRewardVideoAds(context: Context, callback: RewardAdCallback) {
        rewardAdsMap.forEach { (adId, rewardAd) ->
            if (rewardAd == null) {
                val loaderManager = KwaiAdSDK.getKwaiAdLoaderManager()
                if (loaderManager != null) {
                    val loader = loaderManager.buildRewardAdLoader(
                        KwaiRewardAdConfig.Builder(
                            object : AdLoadListener<KwaiRewardAd> {
                                override fun onAdLoadStart(p0: String?) {
                                    Log.d(TAG, "onAdLoadStart() called with: p0 = $p0")
                                }

                                override fun onAdLoadFailed(p0: String?, p1: KwaiError) {
                                    Log.d(TAG, "onAdLoadFailed() called with: p0 = $p0, p1 = $p1")
                                    callback.onFailedToLoad()
                                }

                                override fun onAdLoadSuccess(p0: String?, p1: KwaiRewardAd) {
                                    rewardAdsMap[adId] = p1
                                    Log.d(TAG, "onAdLoadSuccess() called with: p0 = $p0, p1 = $p1")
                                    callback.onLoaded()
                                }
                            })
                            .withKwaiRewardAdListener(object : IKwaiRewardAdListener {
                                override fun onAdShow() {
                                    Log.d(TAG, "onAdShow() called")
                                    callback.onDisplayed()
                                }

                                override fun onAdShowFailed(p0: KwaiError) {
                                    rewardAdsMap[adId] = null
                                    Log.d(TAG, "onAdShowFailed() called with: p0 = $p0")
                                    callback.onFailedToDisplay()
                                }

                                override fun onAdClick() {
                                    Log.d(TAG, "onAdClick() called")
                                    callback.onClicked()
                                }

                                override fun onAdClose() {
                                    rewardAdsMap[adId] = null
                                    Log.d(TAG, "onAdClose() called")
                                    callback.onClosed()
                                }

                                override fun onAdPlayComplete() {
                                    Log.d(TAG, "onAdPlayComplete() called")
                                }

                                override fun onRewardEarned() {
                                    Log.d(TAG, "onRewardEarned() called")
                                    callback.onRewarded()
                                }
                            })
                            .build()
                    )
                    val request = KwaiRewardAdRequest(ApplicationConfiguration.AD_KWAI_REWARD_ID_1)
                    loader.loadAd(request)
                }

            }
        }
    }

    override fun displayHighestRevenueInterstitialAd() {
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val rewardAd =
            rewardAdsMap.values.filterNotNull().maxByOrNull { it.price.toDoubleOrNull() ?: 0.0 }
        rewardAd?.show(activity)
    }

    override fun getHighestRewardAdRevenue(): Double {
        Log.i(
            TAG,
            "getHighestRewardAdRevenue() called with ad platform: ${adConfiguration.adPlatform}, revenueList: ${rewardAdsMap.values.map { it?.price }}"
        )
        return rewardAdsMap.values.filterNotNull().maxOfOrNull { it.price.toDoubleOrNull() ?: 0.0 }
            ?: 0.0
    }

    companion object {
        const val TAG = "KwaiBiddingAdController"
    }

}