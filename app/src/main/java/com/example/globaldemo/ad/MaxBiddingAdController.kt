package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd
import com.example.globaldemo.model.AdConfiguration

class MaxBiddingAdController(override val adConfiguration: AdConfiguration) : BiddingAdController {
    private val rewardAdsMap: MutableMap<String, MaxRewardAdWrapper?> by lazy {
        (adConfiguration.adIdListMap[AdType.REWARD] ?: emptyList())
            .associateWith { null }
            .toMutableMap()
    }

    override fun loadInterstitialAds() {
    }

    override fun loadRewardVideoAds(context: Context, callback: RewardAdCallback) {
        rewardAdsMap.forEach { (adId, rewardAd) ->
            if (rewardAd == null) {
                val rewardedAd = MaxRewardedAd.getInstance(adId, context)
                rewardedAd.setListener(object : MaxRewardedAdListener {
                    override fun onAdLoaded(p0: MaxAd) {
                        rewardAdsMap[adId] = MaxRewardAdWrapper(rewardedAd, p0.revenue)
                        Log.d(TAG, "onAdLoaded() called with: p0 = $p0")
                        callback.onLoaded()
                    }

                    override fun onAdDisplayed(p0: MaxAd) {
                        Log.d(TAG, "onAdDisplayed() called with: p0 = $p0")
                        callback.onDisplayed()
                    }

                    override fun onAdHidden(p0: MaxAd) {
                        Log.d(TAG, "onAdHidden() called with: p0 = $p0")
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
                        callback.onFailedToDisplay()
                    }

                    override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                        Log.d(TAG, "onUserRewarded() called with: p0 = $p0, p1 = $p1")
                        callback.onRewarded()
                    }
                })
                rewardedAd.loadAd()
            }
        }
    }

    override fun displayHighestRevenueInterstitialAd() {
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        val rewardAdWrapper = rewardAdsMap.values.filterNotNull().maxByOrNull { it.revenue }
        rewardAdWrapper?.ad?.showAd(activity)
    }

    override fun getHighestRewardAdRevenue(): Double {
        Log.i(
            TAG,
            "getHighestRewardAdRevenue() called with ad platform: ${adConfiguration.adPlatform}, revenueList: ${rewardAdsMap.values.map { it?.revenue }}"
        )
        return rewardAdsMap.values.filterNotNull().maxOfOrNull { it.revenue } ?: 0.0
    }

    companion object {
        const val TAG = "MaxBiddingAdController"
    }

    private data class MaxRewardAdWrapper(
        val ad: MaxRewardedAd,
        val revenue: Double
    )

}