package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.callback.RewardAdCallback
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.domain.AppDataSourceUseCase
import com.example.globaldemo.model.AdFailureInformation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

/**
 * do ad business logic here
 */
class AdManager(private val appDataSourceUseCase: AppDataSourceUseCase = container.appDataSourceUseCase) {
    private val adControllers: List<BiddingAdController> by lazy {
        runBlocking { getAdControllers() }
    }
    var videoAdShowTimes = 4
        private set

    fun preloadAllRewardAds(context: Context) {
        adControllers.forEach { controller ->
            controller.loadAllRewardVideoAds(
                context,
                eachRewardAdCallback = object : RewardAdCallback {
                    override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                        val nextRetryCount = 2
                        Log.d(
                            TAG,
                            "onFailedToLoad() called with: adInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                        )
                        loadRewardVideoAdsWithRetry(
                            context,
                            adFailureInformation.adId,
                            controller,
                            nextRetryCount
                        )
                    }
                }
            )
        }
    }

    private fun loadRewardVideoAdsWithRetry(
        context: Context,
        adId: String,
        controller: BiddingAdController,
        retryCount: Int
    ) {
        if (retryCount > MAX_LOAD_TIMES) {
            Log.e(TAG, "Failed to load reward ads after $MAX_LOAD_TIMES attempts.")
            return // Stop retrying
        }
        controller.loadSpecificRewardVideoAd(
            context,
            adId,
            callback = object : RewardAdCallback {
                override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                    val nextRetryCount = retryCount + 1
                    Log.d(
                        TAG,
                        "onFailedToLoad() called with: adInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                    )
                    loadRewardVideoAdsWithRetry(context, adId, controller, nextRetryCount)
                }
            }
        )
    }


    fun displayRewardedAd(
        activity: Activity,
        onAdNotAvailableAtFirst: () -> Unit,
        onAdNotAvailableAfter3Second: () -> Unit
    ) {
        val highestRevenueAdController =
            adControllers.maxByOrNull { it.getHighestRewardAdRevenue() }
        if (highestRevenueAdController != null && highestRevenueAdController.getHighestRewardAdRevenue() > 0) {
            highestRevenueAdController.displayHighestRevenueRewardVideoAd(activity)
        } else {
            onAdNotAvailableAtFirst.invoke()
            displayHighestVideoAdAfter3Seconds(activity, onAdNotAvailableAfter3Second)
        }
    }

    private fun displayHighestVideoAdAfter3Seconds(
        activity: Activity,
        onAdNotAvailable: () -> Unit
    ) {
        val highestRevenueAdController =
            adControllers.maxByOrNull { it.getHighestRewardAdRevenue() }
        object : CountDownTimer(3000, 1000) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                if (highestRevenueAdController != null) {
                    highestRevenueAdController.displayHighestRevenueRewardVideoAd(activity)
                } else {
                    // throw exception to outer for callback
                    onAdNotAvailable.invoke()
                }
            }
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
        private const val MAX_LOAD_TIMES = 5
        const val TAG = "AdManager"
    }
}