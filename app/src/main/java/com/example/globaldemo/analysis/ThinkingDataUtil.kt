package com.example.globaldemo.analysis

import android.content.Context
import android.util.Log
import cn.thinkingdata.analytics.TDAnalytics
import cn.thinkingdata.analytics.TDConfig
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.configuration.ApplicationConfiguration
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

object ThinkingDataUtil {
    private const val TAG = "ThinkingDataUtil"
    fun initThinkingDataAnalytics(context: Context) {
        Log.d(TAG, "initThinkingDataAnalytics() called with: context = $context")
        val config = TDConfig.getInstance(
            context,
            ApplicationConfiguration.THINKING_DATA_APP_ID,
            ApplicationConfiguration.THINKING_DATA_SERVER_URL
        ).apply {
            if (BuildConfig.DEBUG) mode = TDConfig.ModeEnum.DEBUG
        }
        TDAnalytics.init(config)
        thread {
            runBlocking {
                launch {
                    val gaid = getGaid()
                    val androidId = getAndroidId()
                    val hasSetDistinctId = hasSetDistinctId()
                    Log.d(
                        TAG,
                        "initThinkingDataAnalytics() called with: gaid = $gaid, androidId = $androidId, hasSetDistinctId = $hasSetDistinctId"
                    )
                    if (!hasSetDistinctId) {
                        if (gaid.isNotEmpty()) TDAnalytics.setDistinctId(gaid)
                        else if (androidId.isNotEmpty()) TDAnalytics.setDistinctId(androidId)
                        else TDAnalytics.setDistinctId(getUUID())
                        updateHasSetDistinctId(true)
                    }
                }
            }
            TDAnalytics.enableAutoTrack(TDAnalytics.TDAutoTrackEventType.APP_END or TDAnalytics.TDAutoTrackEventType.APP_INSTALL)
            Log.d(TAG, "initThinkingDataAnalytics() called with: init completed")
        }
    }

    private suspend fun hasSetDistinctId(): Boolean {
        return GlobalDemoApplication.container.userPreferencesRepository.hasSetDistinctId.first()
    }

    private suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) {
        GlobalDemoApplication.container.userPreferencesRepository.updateHasSetDistinctId(
            hasSetDistinctId
        )
    }

    private fun getGaid(): String {
        return "this is a mock Gaid"
    }

    private fun getAndroidId(): String {
        return "this is a mock AndroidId"
    }

    private fun getUUID(): String {
        return "this is a mock UUID"
    }
}