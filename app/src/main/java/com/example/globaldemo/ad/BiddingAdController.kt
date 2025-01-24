package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import com.example.globaldemo.model.AdConfiguration

interface BiddingAdController {
    val adConfiguration: AdConfiguration
    fun loadInterstitialAds()
    fun loadRewardVideoAds(context: Context, callback: RewardAdCallback = object : RewardAdCallback {})
    fun displayHighestRevenueInterstitialAd()
    fun displayHighestRevenueRewardVideoAd(activity: Activity)
    fun getHighestRewardAdRevenue(): Double = 0.0
}