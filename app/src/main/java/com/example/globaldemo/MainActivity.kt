package com.example.globaldemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.AdControllerFactory
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ui.screen.test.BiddingAdTestScreen
import com.example.globaldemo.ui.screen.test.InterstitialAdPlatformTestScreen
import com.example.globaldemo.ui.screen.test.NetworkTestScreen
import com.example.globaldemo.ui.screen.test.RewardAdPlatformTestScreen
import com.example.globaldemo.ui.theme.GlobalDemoTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

class MainActivity : FragmentActivity() {
    private val controllers: List<BiddingAdController> by lazy {
        runBlocking { getAdControllers() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlobalDemoTheme {
                InterstitialAdPlatformTestScreen(
                    modifier = Modifier.fillMaxSize(),
                    biddingAdControllers = controllers
                )
                /*RewardAdPlatformTestScreen(
                    modifier = Modifier.fillMaxSize(),
                    biddingAdControllers = controllers
                )*/
//                BiddingAdTestScreen()
//                NetworkTestScreen()
            }
        }
    }

    private suspend fun getAdControllers(): List<BiddingAdController> = coroutineScope {
        val appDataSourceUseCase = container.appDataSourceUseCase
        val deferredConfigs = listOf(
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.BIGO) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.KWAI) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.MAX) },
            async { appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.ADMOB) }
        )
        val adConfigurations = deferredConfigs.awaitAll()
        return@coroutineScope AdControllerFactory.generateAdControllers(adConfigurations)
    }

}

