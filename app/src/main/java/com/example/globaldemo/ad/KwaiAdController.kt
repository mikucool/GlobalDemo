package com.example.globaldemo.ad

import android.app.Activity
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
                        }

                        override fun onAdLoadFailed(p0: String?, p1: KwaiError) {
                        }

                        override fun onAdLoadSuccess(p0: String?, p1: KwaiRewardAd) {
                            rewardAd = p1
                        }
                    })
                    .withKwaiRewardAdListener(object : IKwaiRewardAdListener {
                        override fun onAdShow() {
                        }

                        override fun onAdShowFailed(p0: KwaiError) {
                        }

                        override fun onAdClick() {
                        }

                        override fun onAdClose() {
                        }

                        override fun onAdPlayComplete() {
                        }

                        override fun onRewardEarned() {
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