package com.example.globaldemo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.BigoBiddingAdController
import com.example.globaldemo.ad.controller.KwaiBiddingAdController
import com.example.globaldemo.ad.controller.MaxBiddingAdController
import com.example.globaldemo.ui.screen.test.AdTestScreen
import com.example.globaldemo.ui.theme.GlobalDemoTheme
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
                AdTestScreen(
                    modifier = Modifier.fillMaxSize(),
                    biddingAdControllers = controllers
                )
            }
        }
    }

    private suspend fun getAdControllers(): List<BiddingAdController> {
        val appDataSourceUseCase = container.appDataSourceUseCase
        val bigoAdController = BigoBiddingAdController(
            appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.BIGO)
        )
        val kwaiAdController = KwaiBiddingAdController(
            appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.KWAI)
        )
        val maxAdController = MaxBiddingAdController(
            appDataSourceUseCase.fetchAdConfigurationByAdPlatform(AdPlatform.MAX)
        )
        return listOf(bigoAdController, kwaiAdController, maxAdController)
    }

}

