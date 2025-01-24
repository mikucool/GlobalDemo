package com.example.globaldemo.ad

import android.app.Activity
import com.example.globaldemo.model.AdConfiguration

interface BiddingAdController {
    val adConfiguration: AdConfiguration
    val activity: Activity
    fun loadInterstitialAds()
    fun loadRewardVideoAds(callback: RewardAdCallback = object : RewardAdCallback {})
    fun displayHighestRevenueInterstitialAd()
    fun displayHighestRevenueRewardVideoAd()
    fun getHighestRewardAdRevenue(): Double = 0.0
}