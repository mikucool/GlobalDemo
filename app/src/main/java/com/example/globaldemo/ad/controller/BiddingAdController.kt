package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.model.AdConfiguration

interface BiddingAdController {
    val adConfiguration: AdConfiguration

    /**
     * first: adId, second: AdWrapper
     */
    val videoAdsMap: MutableMap<String, AdWrapper>

    fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: RewardAdCallback = object : RewardAdCallback {}
    ) = Unit

    fun loadAllInterstitialAds(
        context: Context,
        eachInterstitialAdCallback: InterstitialAdCallback = object : InterstitialAdCallback {}
    ) = Unit

    fun loadSpecificRewardVideoAd(context: Context, adId: String, callback: RewardAdCallback) = Unit
    fun loadSpecificInterstitialAd(context: Context, adId: String, callback: InterstitialAdCallback) = Unit

    fun displayHighestRevenueInterstitialAd(activity: Activity)
    fun displayHighestRevenueRewardVideoAd(activity: Activity)
    fun getBestAd(): AdWrapper? = null
}