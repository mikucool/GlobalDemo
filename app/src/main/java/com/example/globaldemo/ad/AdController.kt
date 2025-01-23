package com.example.globaldemo.ad

import android.app.Activity

interface AdController {
    val activity: Activity
    val adPlatform: AdPlatform
    fun loadInterstitialAd()
    fun loadRewardVideoAd(callback: RewardAdCallback = object : RewardAdCallback {})
    fun showInterstitialAd()
    fun showRewardVideoAd()
}