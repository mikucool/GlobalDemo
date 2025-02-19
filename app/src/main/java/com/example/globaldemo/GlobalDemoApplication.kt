package com.example.globaldemo

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.example.globaldemo.ad.AdSdkInitializer
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.analytic.AdjustHelper
import com.example.globaldemo.analytic.FirebaseHelper
import com.example.globaldemo.analytic.SMHelper
import com.example.globaldemo.analytic.ThinkingDataHelper
import com.example.globaldemo.model.AdjustInitConfiguration

class GlobalDemoApplication : Application() {
    val adSdkViewModel: AdSdkViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(this).create(AdSdkViewModel::class.java)
    }

    companion object {
        lateinit var instance: GlobalDemoApplication
            private set
        lateinit var container: AppContainer
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(this)
        initSdk()
    }

    private fun initSdk() {
        initializedAdSdk()
        FirebaseHelper.initFirebase()
        ThinkingDataHelper.initialize(this)
        SMHelper.initialize(this)
        AdjustHelper.startAttribute(context = this, AdjustInitConfiguration.OnAppStart::class)
    }

    private fun initializedAdSdk() {
        AdSdkInitializer.initAdSdk(
            context = this,
            platform = AdPlatform.KWAI,
            onInitialized = {
                adSdkViewModel.updateAdSdkInitState(
                    adSdkViewModel.adSdkInitState.value!!.copy(isKwaiInitialized = true)
                )
                container.adManager.loadBiddingVideoAdsByAdPlatform(this, AdPlatform.KWAI)
            }
        )
        AdSdkInitializer.initAdSdk(
            context = this,
            platform = AdPlatform.BIGO,
            onInitialized = {
                adSdkViewModel.updateAdSdkInitState(
                    adSdkViewModel.adSdkInitState.value!!.copy(isBigoInitialized = true)
                )
                container.adManager.loadBiddingVideoAdsByAdPlatform(this, AdPlatform.BIGO)
            }
        )
        AdSdkInitializer.initAdSdk(
            context = this,
            platform = AdPlatform.MAX,
            onInitialized = {
                adSdkViewModel.updateAdSdkInitState(
                    adSdkViewModel.adSdkInitState.value!!.copy(isMaxInitialized = true)
                )
                container.adManager.loadBiddingVideoAdsByAdPlatform(this, AdPlatform.MAX)
            }
        )
        AdSdkInitializer.initAdSdk(
            context = this,
            platform = AdPlatform.ADMOB,
            onInitialized = {
                adSdkViewModel.updateAdSdkInitState(
                    adSdkViewModel.adSdkInitState.value!!.copy(isAdMobInitialized = true)
                )
            }
        )
    }
}