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

    fun loadRewardVideoAds(
        context: Context,
        callback: RewardAdCallback = object : RewardAdCallback {}
    )

    fun displayHighestRevenueInterstitialAd(activity: Activity)
    fun displayHighestRevenueRewardVideoAd(activity: Activity)
    fun getHighestRewardAdRevenue(): Double = 0.0
}