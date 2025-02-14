package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.model.AdConfiguration
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * 虽然实现 BiddingAdController，但是业务不要求 AdMob 广告的客户端比价，所以这里没有实现
 * AdMob业务需求：插屏、开屏、banner、native
 */
class AdMobController(override val adConfiguration: AdConfiguration) : BiddingAdController {
    private val interstitialAdId: String by lazy {
        adConfiguration.adIdListMap[AdType.INTERSTITIAL]?.firstOrNull() ?: ""
    }
    private var interstitialAd: InterstitialAd? = null

    override fun loadInterstitialAds(context: Context, callback: InterstitialAdCallback) {
        Log.d(TAG, "loadInterstitialAds() called with: context = $context, callback = $callback")
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            interstitialAdId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(p0: InterstitialAd) {
                    Log.d(TAG, "onAdLoaded() called with: p0 = $p0")
                    super.onAdLoaded(p0)
                    interstitialAd = p0
                    interstitialAd?.onPaidEventListener = OnPaidEventListener {
                        Log.d(TAG, "onAdPaid() called with: p0 = $it")
                    }
                    callback.onLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad() called with: p0 = $p0")
                    super.onAdFailedToLoad(p0)
                    callback.onFailedToLoad()
                }

            }
        )
    }

    private fun showInterstitialAd(activity: Activity) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                interstitialAd = null
                loadInterstitialAds(activity)
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show fullscreen content.")
                interstitialAd = null
                loadInterstitialAds(activity)
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        if (interstitialAd != null) {
            interstitialAd?.show(activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            loadInterstitialAds(activity)
        }
    }

    override fun loadRewardVideoAds(context: Context, callback: RewardAdCallback) {
        throw UnsupportedOperationException("AdMob does not support this operation")
    }

    override fun displayHighestRevenueInterstitialAd(activity: Activity) {
        showInterstitialAd(activity)
    }

    override fun displayHighestRevenueRewardVideoAd(activity: Activity) {
        throw UnsupportedOperationException("AdMob does not support this operation")
    }

    companion object {
        private const val TAG = "AdMobController"
    }
}