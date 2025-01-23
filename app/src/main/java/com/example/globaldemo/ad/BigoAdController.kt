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

class BigoAdController(override val activity: Activity) : AdController {
    private var rewardAd: RewardVideoAd? = null
    override val adPlatform: AdPlatform = AdPlatform.BIGO
    override fun loadInterstitialAd() {}

    override fun loadRewardVideoAd(callback: RewardAdCallback) {
        val request = RewardVideoAdRequest.Builder()
            .withSlotId(ApplicationConfiguration.AD_BIGO_REWARD_ID)
            .build()
        val rewardVideoAdLoader = RewardVideoAdLoader.Builder()
            .withAdLoadListener(object : AdLoadListener<RewardVideoAd> {
                override fun onError(p0: AdError) {
                    Log.d(TAG, "onError() called with: p0 = $p0")
                    callback.onFailedToLoad()
                }

                override fun onAdLoaded(p0: RewardVideoAd) {
                    Log.d(TAG, "onAdLoaded() called with: p0 = ${p0.bid?.price}")
                    rewardAd = p0
                    rewardAd?.setAdInteractionListener(object : RewardAdInteractionListener{
                        override fun onAdError(p0: AdError) {
                            Log.d(TAG, "onAdError() called with: p0 = $p0")
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
        rewardVideoAdLoader.loadAd(request)
    }

    override fun showInterstitialAd() {
    }

    override fun showRewardVideoAd() {
        if (rewardAd != null && !rewardAd!!.isExpired) {
            rewardAd!!.show(activity)
        }
    }

    companion object {
        private const val TAG = "BigoAdController"
    }
}