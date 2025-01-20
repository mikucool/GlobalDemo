package com.example.globaldemo

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.globaldemo.ad.BigoAdController
import com.example.globaldemo.ad.KwaiAdController
import com.example.globaldemo.ui.screen.ad.AdTestScreen
import com.example.globaldemo.ui.theme.GlobalDemoTheme

class MainActivity : FragmentActivity() {
    private val kwaiAdController by lazy { KwaiAdController() }
    private val bigoAdController by lazy { BigoAdController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlobalDemoTheme {
                AdTestScreen(
                    modifier = Modifier.fillMaxSize(),
                    onLoadAdClicked = { bigoAdController.loadRewardVideoAd() },
                    onShowAdClicked = { bigoAdController.showRewardVideoAd(this as Activity) }
                )
            }
        }
    }
}

