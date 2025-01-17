package com.example.globaldemo.ad

import android.app.Activity

interface AdController {
    fun loadInterstitialAd()
    fun loadRewardVideoAd()
    fun showInterstitialAd()
    fun showRewardVideoAd(activity: Activity)
}