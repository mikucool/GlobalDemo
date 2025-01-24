package com.example.globaldemo.ad

import android.content.Context
import android.os.CountDownTimer
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.factory.AdControllerFactory
import com.example.globaldemo.data.repository.AppDataSourceUseCase
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

    fun loadAllRewardAds(context: Context) {
        adControllers.forEach { controller ->
            controller.loadRewardVideoAds(context)
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