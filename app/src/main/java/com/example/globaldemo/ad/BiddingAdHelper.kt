package com.example.globaldemo.ad

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.constant.AdType
import com.example.globaldemo.ad.controller.AdWrapper
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.utils.AdInfoLogUtil
import com.example.globaldemo.domain.AppDataSourceUseCase
import com.example.globaldemo.model.AdFailureInformation
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.math.pow

/**
 * 处理 Kwai，Bigo 和 MAX 相关竞价业务
 */
class BiddingAdHelper(private val appDataSourceUseCase: AppDataSourceUseCase = container.appDataSourceUseCase) {
    private val adControllers: List<BiddingAdController> by lazy {
        runBlocking { getAdControllers() }
    }

    private val adLoadingStatusMap: MutableMap<String, FailureAdLoadingStatus> = mutableMapOf()

    /**
     * 展示广告：如果没有广告，3 秒后再去检查缓存是否有广告，然后展示
     */
    internal fun tryToDisplayVideoAdWithDelay(
        activity: Activity,
        onAdNotAvailable: () -> Unit = {},
        videoAdShowCallback: VideoAdShowCallback = object : VideoAdShowCallback {}
    ) {
        val bestAdController = findBestAdController()
        if (bestAdController != null) {
            Log.d(TAG, "Displaying ad immediately.")
            bestAdController.displayBestVideoAd(activity, videoAdShowCallback)
            loadFailureVideoAd(activity)
        } else {
            Log.d(TAG, "No ad available immediately. Waiting for $AD_DISPLAY_DELAY_MS ms.")
            waitForAdAvailability(activity, onAdNotAvailable, videoAdShowCallback)
        }
    }

    fun loadVideoAdsByAdPlatform(context: Context, adPlatform: AdPlatform) {
        Log.d(TAG, "loadVideoAdsByAdPlatform() called with: adPlatform = $adPlatform")
        val controller = adControllers.find { it.adConfiguration.adPlatform == adPlatform }
        controller?.loadAllRewardVideoAds(
            context = context,
            eachRewardAdCallback = object : VideoAdLoadCallback {
                override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                    val nextRetryCount = 2
                    Log.e(
                        TAG,
                        "onFailedToLoad() called with: adInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                    )
                    loadVideoAdWithRetry(
                        context,
                        adFailureInformation.adId,
                        adFailureInformation.adType,
                        controller,
                        nextRetryCount
                    )
                }
            }
        )
        controller?.loadAllInterstitialAds(
            context = context,
            eachInterstitialAdCallback = object : VideoAdLoadCallback {
                override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                    val nextRetryCount = 2
                    Log.d(
                        TAG,
                        "onFailedToLoad() called with: adInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                    )
                    loadVideoAdWithRetry(
                        context,
                        adFailureInformation.adId,
                        adFailureInformation.adType,
                        controller,
                        nextRetryCount
                    )
                }
            }
        )
    }

    /**
     * 对单个广告源（max/bigo/kwai），加载失败5次后，判断缓存池中是否有广告源，
     *   - 若缓存池没有广告，第N次重新加载的请求间隔是2的(N-5)次方秒(s)，请求成功后，请求间隔重新累计；
     *   - 若缓存池中存在任意一条广告源（max/bigo/kwai），则在用户下一次触发广告时，从第 N 次加载
     */
    private fun loadVideoAdWithRetry(
        context: Context,
        adId: String,
        adType: AdType,
        controller: BiddingAdController,
        retryCount: Int
    ) {
        Log.w(
            TAG, "loadVideoAdWithRetry() called with: retryCount = $retryCount, " +
                    "ad = ${controller.getAdWrapperById(adId)},"
        )
        if (retryCount > MAX_LOAD_TIMES) {
            Log.e(
                TAG,
                "Failed to load reward ads after $MAX_LOAD_TIMES attempts, current retry count: $retryCount."
            )
            if (checkIfAnyVideoAdLoaded()) {
                adLoadingStatusMap[adId] = FailureAdLoadingStatus(
                    adId = adId,
                    adType = adType,
                    retryCount = retryCount
                )
                Log.d(TAG, "save adLoadingStatus: ${adLoadingStatusMap[adId]}")
                return
            } else {
                val delayTime = getDelayTime(retryCount)
                Handler(Looper.getMainLooper()).postDelayed({
                    when (adType) {
                        AdType.REWARD -> {
                            controller.loadSpecificRewardVideoAd(
                                context,
                                adId,
                                callback = object : VideoAdLoadCallback {
                                    override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                                        val nextRetryCount = retryCount + 1
                                        Log.e(
                                            TAG,
                                            "onFailedToLoad() called with: adFailureInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                                        )
                                        loadVideoAdWithRetry(
                                            context,
                                            adId,
                                            adType,
                                            controller,
                                            nextRetryCount
                                        )
                                    }

                                    override fun onLoaded() {
                                        super.onLoaded()
                                        adLoadingStatusMap.remove(adId)
                                    }
                                }
                            )
                        }

                        AdType.INTERSTITIAL -> {
                            controller.loadSpecificInterstitialAd(
                                context,
                                adId,
                                callback = object : VideoAdLoadCallback {
                                    override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                                        val nextRetryCount = retryCount + 1
                                        Log.e(
                                            TAG,
                                            "onFailedToLoad() called with: adFailureInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                                        )
                                        loadVideoAdWithRetry(
                                            context,
                                            adId,
                                            adType,
                                            controller,
                                            nextRetryCount
                                        )
                                    }

                                    override fun onLoaded() {
                                        super.onLoaded()
                                        adLoadingStatusMap.remove(adId)
                                    }
                                }
                            )
                        }

                        else -> return@postDelayed
                    }
                }, delayTime)
            }
        } else {
            when (adType) {
                AdType.REWARD -> {
                    controller.loadSpecificRewardVideoAd(
                        context,
                        adId,
                        callback = object : VideoAdLoadCallback {
                            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                                val nextRetryCount = retryCount + 1
                                Log.e(
                                    TAG,
                                    "onFailedToLoad() called with: adFailureInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                                )
                                loadVideoAdWithRetry(
                                    context,
                                    adId,
                                    adType,
                                    controller,
                                    nextRetryCount
                                )
                            }
                        }
                    )
                }

                AdType.INTERSTITIAL -> {
                    controller.loadSpecificInterstitialAd(
                        context,
                        adId,
                        callback = object : VideoAdLoadCallback {
                            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                                val nextRetryCount = retryCount + 1
                                Log.e(
                                    TAG,
                                    "onFailedToLoad() called with: adFailureInformation = $adFailureInformation, nextRetryCount = $nextRetryCount"
                                )
                                loadVideoAdWithRetry(
                                    context,
                                    adId,
                                    adType,
                                    controller,
                                    nextRetryCount
                                )
                            }
                        }
                    )
                }

                else -> return
            }

        }
    }

    private fun checkIfAnyVideoAdLoaded(): Boolean {
        Log.d(TAG, "checkIfAnyVideoAdLoaded() called")
        AdInfoLogUtil.logControllersAdInfo(TAG, adControllers)
        adControllers.forEach { controller ->
            // get count of adInstance != null
            val count = controller.videoAdsMap.count { it.value.adInstance != null }
            if (count > 0) return true
        }
        return false
    }

    private fun getDelayTime(retryCount: Int): Long {
        return 2.0.pow(retryCount - 5).toLong() * 1000
    }

    private suspend fun getAdControllers(): List<BiddingAdController> = coroutineScope {
        val deferredConfigs = listOf(
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.BIGO) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.KWAI) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.MAX) }
        )
        val adConfigurations = deferredConfigs.awaitAll()
        return@coroutineScope AdControllerFactory.createBiddingControllers(adConfigurations)
    }

    data class FailureAdLoadingStatus(
        val adId: String,
        val adType: AdType,
        val retryCount: Int,
    )

    private fun waitForAdAvailability(
        activity: Activity,
        onAdNotAvailable: () -> Unit,
        videoAdShowCallback: VideoAdShowCallback
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            val bestAdController = findBestAdController()
            if (bestAdController != null) {
                Log.d(TAG, "Displaying ad after delay.")
                bestAdController.displayBestVideoAd(activity, videoAdShowCallback)
                loadFailureVideoAd(activity)
            } else {
                Log.d(TAG, "No ad available after delay.")
                onAdNotAvailable()
            }
        }, AD_DISPLAY_DELAY_MS)
    }

    private fun findBestAdController(): BiddingAdController? {
        val adController = adControllers.maxByOrNull { it.getBestVideoAd()?.adRevenue ?: 0.0 }
        return if ((adController?.getBestVideoAd()?.adRevenue ?: 0.0) > 0) adController else null
    }

    private fun BiddingAdController.getAdWrapperById(adId: String): AdWrapper? {
        return this.videoAdsMap[adId]
    }

    private fun loadFailureVideoAd(context: Context) {
        adLoadingStatusMap.values.forEach { failureAdLoadingStatus ->
            loadVideoAdWithRetry(
                context,
                failureAdLoadingStatus.adId,
                failureAdLoadingStatus.adType,
                findBestAdController()!!,
                failureAdLoadingStatus.retryCount
            )
        }
    }

    companion object {
        private const val MAX_LOAD_TIMES = 5
        private const val AD_DISPLAY_DELAY_MS = 3000L
        const val TAG = "BiddingAdHelper"
    }
}