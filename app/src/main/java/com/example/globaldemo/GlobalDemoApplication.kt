package com.example.globaldemo

import android.app.Application
import com.example.globaldemo.ad.AdSdkInitializer
import com.example.globaldemo.analytic.SMInitializer
import com.example.globaldemo.analytic.ThinkingDataInitializer

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
        ThinkingDataInitializer.initialize(this)
        SMInitializer.initialize(this)
    }
}