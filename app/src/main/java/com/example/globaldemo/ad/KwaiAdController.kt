package com.example.globaldemo.ad

import android.app.Activity
import android.util.Log
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.constant.KwaiError
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAd
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdConfig
import com.kwai.network.sdk.loader.business.reward.data.KwaiRewardAdRequest
import com.kwai.network.sdk.loader.business.reward.interf.IKwaiRewardAdListener
import com.kwai.network.sdk.loader.common.interf.AdLoadListener

class KwaiAdController : AdController {
    private var rewardAd: KwaiRewardAd? = null
    override fun loadInterstitialAd() {

    }

    override fun loadRewardVideoAd() {
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
                        }

                        override fun onAdLoadSuccess(p0: String?, p1: KwaiRewardAd) {
                            rewardAd = p1
                            Log.d(TAG, "onAdLoadSuccess() called with: p0 = $p0, p1 = $p1")
                        }
                    })
                    .withKwaiRewardAdListener(object : IKwaiRewardAdListener {
                        override fun onAdShow() {
                            Log.d(TAG, "onAdShow() called")
                        }

                        override fun onAdShowFailed(p0: KwaiError) {
                            Log.d(TAG, "onAdShowFailed() called with: p0 = $p0")
                        }

                        override fun onAdClick() {
                            Log.d(TAG, "onAdClick() called")
                        }

                        override fun onAdClose() {
                            Log.d(TAG, "onAdClose() called")
                        }

                        override fun onAdPlayComplete() {
                            Log.d(TAG, "onAdPlayComplete() called")
                        }

                        override fun onRewardEarned() {
                            Log.d(TAG, "onRewardEarned() called")
                        }
                    })
                    .build()
            )
            val request = KwaiRewardAdRequest(ApplicationConfiguration.AD_KWAI_REWARD_ID)
            loader.loadAd(request)
        }
    }

    override fun showInterstitialAd() {
    }

    override fun showRewardVideoAd(activity: Activity) {
        if (rewardAd != null && rewardAd!!.isReady) {
            rewardAd!!.show(activity)
        }
    }

    companion object {
        const val TAG = "KwaiAdController"
    }

}