package com.example.globaldemo.ad.controller

import android.app.Activity
import android.content.Context
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.model.AdConfiguration

interface BiddingAdController {
    val adConfiguration: AdConfiguration
    fun loadInterstitialAds(
        context: Context,
        callback: InterstitialAdCallback = object : InterstitialAdCallback {}
    )

    fun loadAllRewardVideoAds(
        context: Context,
        eachRewardAdCallback: RewardAdCallback = object : RewardAdCallback {}
    )

    fun loadSpecificRewardVideoAd(context: Context, adId: String, callback: RewardAdCallback) = Unit

    fun displayHighestRevenueInterstitialAd(activity: Activity)
    fun displayHighestRevenueRewardVideoAd(activity: Activity)
    fun getHighestRewardAdRevenue(): Double = 0.0
}