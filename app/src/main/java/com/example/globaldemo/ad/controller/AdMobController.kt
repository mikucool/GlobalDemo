package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.model.AdFailureInformation
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.Date

/**
 * 虽然实现 BiddingAdController，但是业务不要求 AdMob 广告的客户端比价，所以这里没有实现
 * AdMob业务需求：插屏、开屏、banner、native
 */
class AdMobController(val adConfiguration: AdConfiguration) {
    private val interstitialAdId: String by lazy {
        adConfiguration.adIdListMap[AdType.INTERSTITIAL]?.firstOrNull() ?: ""
    }
    private val splashAdId: String by lazy {
        adConfiguration.adIdListMap[AdType.SPLASH]?.firstOrNull() ?: ""
    }

    private var interstitialAd: InterstitialAd? = null
    private var splashAd: AppOpenAd? = null

    // prevent memory leaking
    private var interstitialLoadCallback: VideoAdLoadCallback? = null
    fun clearInterstitialLoadCallback() = run { interstitialLoadCallback = null }
    private var interstitialAdLoadedTime = 0L
    fun loadInterstitialAd(callback: VideoAdLoadCallback = object : VideoAdLoadCallback {}) {
        val context: Context = GlobalDemoApplication.instance
        interstitialLoadCallback = callback
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
                    interstitialAdLoadedTime = Date().time
                    interstitialAd?.onPaidEventListener = OnPaidEventListener {
                        Log.d(TAG, "onAdPaid() called with: p0 = $it")
                    }
                    interstitialLoadCallback?.onLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad() called with: p0 = $p0")
                    super.onAdFailedToLoad(p0)
                    interstitialLoadCallback?.onFailedToLoad(
                        AdFailureInformation(
                            platform = adConfiguration.adPlatform,
                            adId = interstitialAdId,
                            adType = AdType.INTERSTITIAL
                        )
                    )
                }

            }
        )
    }

    // prevent memory leaking
    private var interstitialDisplayCallback: VideoAdShowCallback? = null
    fun clearInterstitialDisplayCallback() = run { interstitialDisplayCallback = null }
    fun displayInterstitialAd(activity: Activity, callback: VideoAdShowCallback) {
        if (!isInterstitialAdAvailable()) {
            callback.onFailedToDisplay()
            return
        }
        interstitialDisplayCallback = callback
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
                interstitialDisplayCallback?.onClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                interstitialAd = null
                interstitialDisplayCallback?.onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                // Called when ad fails to show.
                Log.d(TAG, "Ad failed to show fullscreen content.")
                interstitialAd = null
                interstitialDisplayCallback?.onFailedToDisplay()
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
                interstitialDisplayCallback?.onDisplayed()
            }
        }

        interstitialAd?.show(activity)

    }

    // prevent memory leaking
    private var splashLoadCallback: VideoAdLoadCallback? = null
    fun clearSplashLoadCallback() = run { splashLoadCallback = null }
    private var splashAdLoadedTime = 0L
    fun loadSplashAd(callback: VideoAdLoadCallback = object : VideoAdLoadCallback {}) {
        val context: Context = GlobalDemoApplication.instance
        splashLoadCallback = callback
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            splashAdId,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(p0: AppOpenAd) {
                    super.onAdLoaded(p0)
                    Log.d(TAG, "onAdLoaded() called with: p0 = $p0")
                    splashAd = p0
                    splashAdLoadedTime = Date().time
                    splashLoadCallback?.onLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                    Log.d(TAG, "onAdFailedToLoad() called with: p0 = $p0")
                    splashLoadCallback?.onFailedToLoad(
                        AdFailureInformation(
                            platform = adConfiguration.adPlatform,
                            adId = splashAdId,
                            adType = AdType.SPLASH
                        )
                    )
                }
            }
        )
    }

    // prevent memory leaking
    private var splashDisplayCallback: VideoAdShowCallback? = null
    fun clearSplashDisplayCallback() = run { splashDisplayCallback = null }
    fun displaySplashActivity(
        activity: Activity,
        callback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) {
        splashDisplayCallback = callback
        if (!isSplashAdAvailable()) {
            callback.onFailedToDisplay()
            loadSplashAd()
            return
        }
        splashAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                Log.d(TAG, "onAdShowedFullScreenContent() called")
                splashDisplayCallback?.onDisplayed()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                Log.d(TAG, "onAdFailedToShowFullScreenContent() called with: p0 = $p0")
                splashAd = null
                splashDisplayCallback?.onFailedToDisplay()
                loadSplashAd()
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "onAdClicked() called")
                splashDisplayCallback?.onClicked()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Log.d(TAG, "onAdDismissedFullScreenContent() called")
                splashAd = null
                splashDisplayCallback?.onClosed()
                loadSplashAd()
            }
        }
        splashAd?.show(activity)
    }

    private fun isSplashAdAvailable(): Boolean {
        return splashAd != null && wasLoadTimeLessThanNHoursAgo(SPLASH_EXPIRED_HOUR_TIME, splashAdLoadedTime)
    }

    private fun isInterstitialAdAvailable(): Boolean {
        return interstitialAd != null && wasLoadTimeLessThanNHoursAgo(INTERSTITIAL_EXPIRED_HOUR_TIME, interstitialAdLoadedTime)
    }

    /** Utility method to check if ad was loaded more than n hours ago. */
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long, adLoadedTime: Long): Boolean {
        val dateDifference: Long = Date().time - adLoadedTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    companion object {
        private const val TAG = "AdMobController"
        private const val SPLASH_EXPIRED_HOUR_TIME = 4L
        private const val INTERSTITIAL_EXPIRED_HOUR_TIME = 1L
    }
}