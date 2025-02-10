package com.example.globaldemo

import android.app.Application
import com.example.globaldemo.ad.AdSdkInitializer
import com.example.globaldemo.analysis.ThinkingDataUtil
import com.example.globaldemo.data.AppContainer
import com.example.globaldemo.data.DefaultAppContainer
import com.example.globaldemo.verification.VerificationManager

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
        ThinkingDataUtil.initThinkingDataAnalytics(this)
        VerificationManager.initSMAndQueryID(this)
    }
}