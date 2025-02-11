package com.example.globaldemo.domain

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.AdControllerFactory
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.BiddingAdController
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * do ad business logic here
 */
class AdUseCase(private val appDataSourceUseCase: AppDataSourceUseCase = container.appDataSourceUseCase) {
    private val adControllers: List<BiddingAdController> by lazy {
        runBlocking { getAdControllers() }
    }

    private var adLoadingCountDownTimer: CountDownTimer? = null
    private var isLoadingTimeout = false

    fun preloadAllRewardAds(context: Context) {
        adControllers.forEach { controller -> controller.loadRewardVideoAds(context) }
    }

    fun displayRewardedAd(activity: Activity, onTimeout: () -> Unit = {}) {
        val highestRevenueAdController =
            adControllers.maxByOrNull { it.getHighestRewardAdRevenue() }
        if (highestRevenueAdController != null) {
            highestRevenueAdController.displayHighestRevenueRewardVideoAd(activity)
        } else {
            loadRewardedAdsWithTimeout(
                context = activity,
                onAdAvailable = { displayRewardedAd(activity) },
                onTimeout = onTimeout
            )
        }
    }

    fun preLoadAllInterstitialAds(context: Context) {
        adControllers.forEach { controller -> controller.loadInterstitialAds(context) }
    }

    fun displayInterstitialAd(activity: Activity) {
        // just show Max interstitial ad
        val maxController = adControllers.find { it.adConfiguration.adPlatform == AdPlatform.MAX }
        maxController?.displayHighestRevenueInterstitialAd(activity)
    }

    private fun loadRewardedAdsWithTimeout(
        context: Context,
        onAdAvailable: () -> Unit = {},
        onTimeout: () -> Unit = {}
    ) {
        isLoadingTimeout = false
        adLoadingCountDownTimer = object : CountDownTimer(AD_LOADING_TIMEOUT, 1000) {
            override fun onTick(p0: Long) {
                Log.d(TAG, "onTick() called with: p0 = $p0")
            }

            override fun onFinish() {
                isLoadingTimeout = true
                Log.e(TAG, "adLoadingCountDownTimer onFinish() called, Timeout!")
                onTimeout.invoke()
            }

        }
        adControllers.forEach { controller ->
            controller.loadRewardVideoAds(context, callback = object : RewardAdCallback {
                override fun onLoaded() {
                    if (!isLoadingTimeout && adLoadingCountDownTimer != null) {
                        adLoadingCountDownTimer?.cancel()
                        adLoadingCountDownTimer = null
                        onAdAvailable.invoke()
                    }
                }
            })
        }
    }

    private suspend fun getAdControllers(): List<BiddingAdController> = coroutineScope {
        val deferredConfigs = listOf(
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.BIGO) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.KWAI) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.MAX) }
        )
        val adConfigurations = deferredConfigs.awaitAll()
        return@coroutineScope AdControllerFactory.generateAdControllers(adConfigurations)
    }

    companion object {
        private const val AD_LOADING_TIMEOUT = 3000L
        const val TAG = "AdUseCase"

    }
}