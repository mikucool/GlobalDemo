package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
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
                        loadRewardVideoAdWithRetry(
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

    /**
     * 对单个广告源（max/bigo/kwai），加载失败5次后，判断缓存池中是否有广告源，
     *   - 若缓存池没有广告，第N次重新加载的请求间隔是2的(N-5)次方秒(s)，请求成功后，请求间隔重新累计；
     *   - 若缓存池中存在任意一条广告源（max/bigo/kwai），则在用户下一次触发广告时，重新加载；
     */
    private fun loadRewardVideoAdWithRetry(
        context: Context,
        adId: String,
        controller: BiddingAdController,
        retryCount: Int
    ) {
        if (retryCount > MAX_LOAD_TIMES) {
            Log.e(TAG, "Failed to load reward ads after $MAX_LOAD_TIMES attempts.")
            // TODO: - 若缓存池中存在任意一条广告源（max/bigo/kwai），则在用户下一次触发广告时，重新加载；
            if (checkIfAnyVideoAdReady()) {
                return
            } else {
                // TODO: - 若缓存池没有广告，第N次重新加载的请求间隔是2的(N-5)次方秒(s)，请求成功后，请求间隔重新累计；
            }
        } else {
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
                        loadRewardVideoAdWithRetry(context, adId, controller, nextRetryCount)
                    }
                }
            )
        }
    }

    private fun checkIfAnyVideoAdReady(): Boolean {
        return true
    }

    private fun loadFailureVideoAd(context: Context) {

    }

    fun displayRewardedAd(activity: Activity) {
        val highestRevenueAdController = findBestAdController()
        highestRevenueAdController?.displayHighestRevenueRewardVideoAd(activity)
    }

    /**
     * Attempts to display a video ad, with a fallback mechanism to wait for a specified duration.
     *
     * @param activity The current activity.
     * @param onAdDisplayed Callback invoked when an ad is successfully displayed.
     * @param onAdNotAvailable Callback invoked when no ad is available after the delay.
     */
    fun tryToDisplayVideoAdWithDelay(
        activity: Activity,
        onAdDisplayed: () -> Unit = {},
        onAdNotAvailable: () -> Unit = {},
    ) {
        val bestAdController = findBestAdController()
        if (bestAdController != null) {
            Log.d(TAG, "Displaying ad immediately.")
            bestAdController.displayHighestRevenueRewardVideoAd(activity)
            onAdDisplayed()
        } else {
            Log.d(TAG, "No ad available immediately. Waiting for $AD_DISPLAY_DELAY_MS ms.")
            waitForAdAvailability(activity, onAdDisplayed, onAdNotAvailable)
        }
    }

    private fun waitForAdAvailability(
        activity: Activity,
        onAdDisplayed: () -> Unit,
        onAdNotAvailable: () -> Unit,
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            val bestAdController = findBestAdController()
            if (bestAdController != null) {
                Log.d(TAG, "Displaying ad after delay.")
                bestAdController.displayHighestRevenueRewardVideoAd(activity)
                onAdDisplayed()
            } else {
                Log.d(TAG, "No ad available after delay.")
                onAdNotAvailable()
            }
        }, AD_DISPLAY_DELAY_MS)
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

    private fun findBestAdController(): BiddingAdController? {
        val adController = adControllers.maxByOrNull { it.getBestAd()?.adRevenue ?: 0.0 }
        return if ((adController?.getBestAd()?.adRevenue ?: 0.0) > 0) adController else null
    }

    companion object {
        private const val MAX_LOAD_TIMES = 5
        private const val AD_DISPLAY_DELAY_MS = 3000L
        const val TAG = "AdManager"
    }
}