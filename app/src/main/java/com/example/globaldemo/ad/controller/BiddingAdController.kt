package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.callback.RewardAdCallback
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

    val videoAdShowCallbackMap: MutableMap<AdWrapper, VideoAdShowCallback>

    /**
     * load all reward video ads
     * @param context context
     * @param eachRewardAdCallback callback for each ad
     */
    fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: RewardAdCallback = object : RewardAdCallback {}
    ) = Unit

    /**
     * load all interstitial ads
     * @param context context
     * @param eachInterstitialAdCallback callback for each ad
     */
    fun loadAllInterstitialAds(
        context: Context,
        eachInterstitialAdCallback: InterstitialAdCallback = object : InterstitialAdCallback {}
    ) = Unit

    /**
     * load specific reward video ad
     * @param context context
     * @param adId ad id
     * @param callback callback
     */
    fun loadSpecificRewardVideoAd(context: Context, adId: String, callback: RewardAdCallback) = Unit

    /**
     * load specific interstitial ad
     * @param context context
     * @param adId ad id
     * @param callback callback
     */
    fun loadSpecificInterstitialAd(
        context: Context,
        adId: String,
        callback: InterstitialAdCallback
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

    fun displayHighestRevenueInterstitialAd(activity: Activity) = Unit
    fun displayHighestRevenueRewardVideoAd(activity: Activity) = Unit


    fun getBestVideoAd(): AdWrapper? = null
}