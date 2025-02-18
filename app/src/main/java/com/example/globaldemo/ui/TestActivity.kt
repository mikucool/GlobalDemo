package com.example.globaldemo.ui

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

class TestActivity : FragmentActivity() {
    private val controllers: List<BiddingAdController> by lazy {
        runBlocking { getAdControllers() }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // detect back press for test screen
        enableEdgeToEdge()
        setContent {
            GlobalDemoTheme {
                var testTypeState by remember { mutableStateOf(TestType.NONE) }
                onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        if (testTypeState != TestType.NONE) {
                            testTypeState = TestType.NONE
                        } else {
                            finish()
                        }
                    }
                })
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (testTypeState) {
                            TestType.INTERSTITIAL -> {
                                InterstitialAdPlatformTestScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    biddingAdControllers = controllers
                                )
                            }

                            TestType.REWARD -> {
                                RewardAdPlatformTestScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    biddingAdControllers = controllers
                                )
                            }

                            TestType.NETWORK -> {
                                NetworkTestScreen(modifier = Modifier.fillMaxSize())
                            }

                            TestType.BIDDING -> {
                                BiddingAdTestScreen()
                            }

                            else -> {}
                        }
                    }
                    if (testTypeState == TestType.NONE) {
                        // buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = { testTypeState = TestType.INTERSTITIAL }) {
                                Text(text = "Interstitial")
                            }
                            Button(onClick = { testTypeState = TestType.REWARD }) {
                                Text(text = "Reward")
                            }
                            Button(onClick = { testTypeState = TestType.NETWORK }) {
                                Text(text = "Network")
                            }
                            Button(onClick = { testTypeState = TestType.BIDDING }) {
                                Text(text = "Bidding")
                            }
                        }
                    }
                }
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

    enum class TestType {
        INTERSTITIAL,
        REWARD,
        NETWORK,
        BIDDING,
        NONE
    }
}

