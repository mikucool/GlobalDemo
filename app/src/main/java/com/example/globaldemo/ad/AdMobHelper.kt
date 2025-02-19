package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.AdMobController
import com.example.globaldemo.domain.AppDataSourceUseCase
import kotlinx.coroutines.runBlocking

class AdMobHelper(private val appDataSourceUseCase: AppDataSourceUseCase = container.appDataSourceUseCase) {
    private val controller: AdMobController by lazy {
        runBlocking {
            val config = appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.ADMOB)
            AdControllerFactory.createAdMobControllers(config)
        }
    }

    fun loadInterstitialAd(
        context: Context,
        callback: VideoAdLoadCallback = object : VideoAdLoadCallback {}
    ) = controller.loadInterstitialAd(context, callback)

    fun displayInterstitialAd(
        activity: Activity,
        callback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) = controller.displayInterstitialAd(activity, callback)

    fun loadSplashAd(
        context: Context,
        callback: VideoAdLoadCallback = object : VideoAdLoadCallback {}
    ) = controller.loadSplashAd(context, callback)

    fun displaySplashAd(
        activity: Activity,
        callback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) = controller.displaySplashActivity(activity, callback)

}