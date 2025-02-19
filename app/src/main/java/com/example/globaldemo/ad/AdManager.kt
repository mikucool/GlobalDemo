package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdPlatform

/**
 * Manages ad-related business logic, including loading and displaying bidding and AdMob ads.
 */
class AdManager(
    private val biddingAdHelper: BiddingAdHelper = BiddingAdHelper(),
    private val adMobHelper: AdMobHelper = AdMobHelper()
) {

    /**
     * The number of times a video ad can be shown.
     */
    var videoAdShowTimes: Int = 4
        private set

    /**
     * The list of supported bidding ad platforms.
     */
    private val supportedBiddingAdPlatforms: List<AdPlatform> =
        listOf(AdPlatform.BIGO, AdPlatform.KWAI, AdPlatform.MAX)

    /**
     * Loads video ads for all supported bidding ad platforms.
     *
     * @param context The application context.
     */
    fun loadAllBiddingVideoAds(context: Context) {
        Log.d(TAG, "loadAllBiddingVideoAds() called")
        supportedBiddingAdPlatforms.forEach { platform ->
            biddingAdHelper.loadVideoAdsByAdPlatform(context, platform)
        }
    }

    /**
     * Loads a video ad for a specific bidding ad platform.
     *
     * @param context The application context.
     * @param adPlatform The ad platform to load the video ad for.
     * @throws IllegalArgumentException if the provided ad platform is not supported.
     */
    fun loadBiddingVideoAdsByAdPlatform(context: Context, adPlatform: AdPlatform) {
        Log.d(TAG, "loadBiddingVideoAdsByAdPlatform() called with: adPlatform = $adPlatform")
        require(adPlatform in supportedBiddingAdPlatforms) { "Unsupported ad platform: $adPlatform" }
        biddingAdHelper.loadVideoAdsByAdPlatform(context, adPlatform)
    }

    /**
     * Attempts to display a bidding video ad with a potential delay.
     *
     * @param activity The activity context.
     * @param onAdNotAvailable Callback to be invoked if no ad is available.
     * @param videoAdShowCallback Callback for video ad show events.
     */
    fun displayBiddingVideoAd(
        activity: Activity,
        onAdNotAvailable: () -> Unit = {},
        videoAdShowCallback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) {
        biddingAdHelper.tryToDisplayVideoAdWithDelay(
            activity,
            onAdNotAvailable,
            videoAdShowCallback
        )
    }

    /**
     * Loads an AdMob interstitial ad.
     *
     * @param context The application context.
     * @param callback Callback for video ad load events.
     */
    fun loadAdMobInterstitialAd(context: Context, callback: VideoAdLoadCallback) {
        Log.d(TAG, "loadAdMobInterstitialAd() called")
        adMobHelper.loadInterstitialAd(context, callback)
    }

    /**
     * Displays an AdMob interstitial ad.
     *
     * @param activity The activity context.
     * @param callback Callback for video ad show events.
     */
    fun displayAdMobInterstitialAd(activity: Activity, callback: VideoAdShowCallback) {
        Log.d(TAG, "displayAdMobInterstitialAd() called")
        adMobHelper.displayInterstitialAd(activity, callback)
    }

    /**
     * Loads an AdMob splash ad.
     * @param context The application context.
     * @param callback Callback for video ad load events.
     */
    fun loadAdMobSplashAd(context: Context, callback: VideoAdLoadCallback) {
        Log.d(TAG, "loadAdMobSplashAd() called")
        adMobHelper.loadSplashAd(context, callback)
    }

    /**
     * Displays an AdMob splash ad.
     * @param activity The activity context.
     * @param callback Callback for video ad show events.
     */
    fun displayAdMobSplashAd(activity: Activity, callback: VideoAdShowCallback) {
        Log.d(TAG, "displayAdMobSplashAd() called")
        adMobHelper.displaySplashAd(activity, callback)
    }

    companion object {
        private const val TAG = "AdManager"
    }
}