package com.example.globaldemo.ad

import android.app.Activity

interface AdController {
    val activity: Activity
    val adPlatform: AdPlatform
    fun loadInterstitialAd()
    fun loadRewardVideoAd()
    fun showInterstitialAd()
    fun showRewardVideoAd()
}