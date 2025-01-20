package com.example.globaldemo

import android.app.Application
import com.example.globaldemo.ad.AdUseCase
import com.example.globaldemo.analysis.ThinkingDataUtil
import com.example.globaldemo.data.AppContainer
import com.example.globaldemo.data.DefaultAppContainer
import com.example.globaldemo.identifier.SMUtil

class GlobalDemoApplication : Application() {
    companion object {
        lateinit var container: AppContainer
            private set
    }
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        initSdk()
    }

    private fun initSdk() {
        AdUseCase.initKwaiAd(this)
        AdUseCase.initBigoAd(this)
        ThinkingDataUtil.initThinkingDataAnalytics(this)
        SMUtil.initSM(this)
    }
}