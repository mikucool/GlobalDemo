package com.example.globaldemo

import android.app.Application
import com.example.globaldemo.ad.AdSdkInitializer
import com.example.globaldemo.analytic.AdjustHelper
import com.example.globaldemo.analytic.SMHelper
import com.example.globaldemo.analytic.ThinkingDataHelper
import com.example.globaldemo.model.AdjustInitConfiguration

class GlobalDemoApplication : Application() {
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
        AdSdkInitializer.initKwaiAd(this)
        AdSdkInitializer.initBigoAd(this)
        AdSdkInitializer.initMaxAd(this)
        AdSdkInitializer.initAdMob(this)
        ThinkingDataHelper.initialize(this)
        SMHelper.initialize(this)
        AdjustHelper.startAttribute(context = this, AdjustInitConfiguration.OnAppStart::class)
    }
}