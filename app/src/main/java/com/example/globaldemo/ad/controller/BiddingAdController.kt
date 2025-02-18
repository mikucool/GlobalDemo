package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.model.AdConfiguration

interface BiddingAdController {
    /**
     * ad configuration
     */
    val adConfiguration: AdConfiguration

    /**
     * first: adId, second: AdWrapper
     */
    val videoAdsMap: MutableMap<String, AdWrapper>

    /**
     * first: adId, second: VideoAdShowCallback
     */
    val videoAdShowCallbackMap: MutableMap<String, VideoAdShowCallback>

    /**
     * load all reward video ads
     * @param context context
     * @param eachRewardAdCallback callback for each ad
     */
    fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: VideoAdLoadCallback = object : VideoAdLoadCallback {}
    ) = Unit

    /**
     * load all interstitial ads
     * @param context context
     * @param eachInterstitialAdCallback callback for each ad
     */
    fun loadAllInterstitialAds(
        context: Context,
        eachInterstitialAdCallback: VideoAdLoadCallback = object : VideoAdLoadCallback {}
    ) = Unit

    /**
     * load specific reward video ad
     * @param context context
     * @param adId ad id
     * @param callback callback
     */
    fun loadSpecificRewardVideoAd(
        context: Context,
        adId: String,
        callback: VideoAdLoadCallback
    ) = Unit

    /**
     * load specific interstitial ad
     * @param context context
     * @param adId ad id
     * @param callback callback
     */
    fun loadSpecificInterstitialAd(
        context: Context,
        adId: String,
        callback: VideoAdLoadCallback
    ) = Unit

    /**
     * display best video ad
     * @param activity activity
     * @param videoAdShowCallback callback for video ad show
     */
    fun displayBestVideoAd(
        activity: Activity,
        videoAdShowCallback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) = Unit

    fun displayHighestRevenueInterstitialAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback = object : VideoAdShowCallback {}) = Unit
    fun displayHighestRevenueRewardVideoAd(activity: Activity, videoAdShowCallback: VideoAdShowCallback = object : VideoAdShowCallback {}) = Unit


    fun getBestVideoAd(): AdWrapper? = null
}