package com.example.globaldemo.ad.utils

import android.util.Log
import com.example.globaldemo.ad.controller.BiddingAdController

object AdInfoLogUtil {
    fun logControllersAdInfo(tag: String, controllers: List<BiddingAdController>) {
        // log the ad info for each controller
        Log.i(tag, "===========================Begin=============================")
        controllers.forEach { controller ->
            Log.d(tag, "Controller: ${controller.adConfiguration.adPlatform}")
            controller.videoAdsMap.forEach { (adId, adWrapper) ->
                Log.d(tag, "  Ad ID: $adId, Ad Wrapper: $adWrapper")
            }
            Log.d(tag, "  Best Ad: ${controller.getBestAd()}")
            val loadedAdCount = controller.videoAdsMap.count { it.value.adInstance != null }
            Log.d(tag, "  Loaded Ad Count: $loadedAdCount")
            Log.d(tag, "----------------------------------------------------------")
        }
        Log.i(tag, "============================End============================")
    }
}