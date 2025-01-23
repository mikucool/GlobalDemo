package com.example.globaldemo.ad

import android.app.Activity
import android.util.Log
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd

class MaxAdController(override val activity: Activity) : AdController {
    override val adPlatform: AdPlatform = AdPlatform.MAX
    private lateinit var rewardedAd: MaxRewardedAd
    override fun loadInterstitialAd() {
    }

    override fun loadRewardVideoAd(callback: RewardAdCallback) {
        rewardedAd = MaxRewardedAd.getInstance("«ad-unit-ID»", activity)
        rewardedAd.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(p0: MaxAd) {
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

    override fun showInterstitialAd() {
    }

    override fun showRewardVideoAd() {
        if (rewardedAd.isReady) {
            rewardedAd.showAd(activity)
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    companion object {
        const val TAG = "MaxAdController"
    }

}