package com.example.globaldemo.ad

import android.app.Activity
import android.util.Log
import com.example.globaldemo.configuration.ApplicationConfiguration
import sg.bigo.ads.api.AdError
import sg.bigo.ads.api.AdLoadListener
import sg.bigo.ads.api.RewardAdInteractionListener
import sg.bigo.ads.api.RewardVideoAd
import sg.bigo.ads.api.RewardVideoAdLoader
import sg.bigo.ads.api.RewardVideoAdRequest

class BigoAdController : AdController {
    private var rewardAd: RewardVideoAd? = null
    override fun loadInterstitialAd() {}

    override fun loadRewardVideoAd() {
        val request = RewardVideoAdRequest.Builder()
            .withSlotId(ApplicationConfiguration.AD_BIGO_REWARD_ID)
            .build()
        val rewardVideoAdLoader = RewardVideoAdLoader.Builder()
            .withAdLoadListener(object : AdLoadListener<RewardVideoAd> {
                override fun onError(p0: AdError) {
                    Log.d(TAG, "onError() called with: p0 = $p0")
                }

                override fun onAdLoaded(p0: RewardVideoAd) {
                    Log.d(TAG, "onAdLoaded() called with: p0 = ${p0.bid?.price}")
                    rewardAd = p0
                }
            })
            .build()
        rewardVideoAdLoader.loadAd(request)
    }

    override fun showInterstitialAd() {
    }

    override fun showRewardVideoAd(activity: Activity) {
        if (rewardAd != null && !rewardAd!!.isExpired) {
            rewardAd!!.setAdInteractionListener(object : RewardAdInteractionListener{
                override fun onAdError(p0: AdError) {
                    Log.d(TAG, "onAdError() called with: p0 = $p0")
                }

                override fun onAdImpression() {
                    Log.d(TAG, "onAdImpression() called")
                }

                override fun onAdClicked() {
                    Log.d(TAG, "onAdClicked() called")
                }

                override fun onAdOpened() {
                    Log.d(TAG, "onAdOpened() called")
                }

                override fun onAdClosed() {
                    Log.d(TAG, "onAdClosed() called")
                }

                override fun onAdRewarded() {
                    Log.d(TAG, "onAdRewarded() called")
                }
            })
            rewardAd!!.show(activity)
        }
    }

    companion object {
        private const val TAG = "BigoAdController"
    }
}