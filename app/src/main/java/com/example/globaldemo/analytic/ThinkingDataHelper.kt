package com.example.globaldemo.analytic

import android.content.Context
import android.util.Log
import cn.thinkingdata.analytics.TDAnalytics
import cn.thinkingdata.analytics.TDConfig
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.configuration.ApplicationConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThinkingDataHelper {

    private const val TAG = "ThinkingDataAnalyticsManager"

    fun initialize(context: Context) {
        Log.d(TAG, "initialize() called with: context = $context")

        // Configure ThinkingData SDK
        val config = TDConfig.getInstance(
            context,
            ApplicationConfiguration.THINKING_DATA_APP_ID,
            ApplicationConfiguration.THINKING_DATA_SERVER_URL
        ).apply {
            if (BuildConfig.DEBUG) mode = TDConfig.ModeEnum.DEBUG
        }
        TDAnalytics.init(config)

        // Launch initialization tasks in a background coroutine
        CoroutineScope(Dispatchers.IO).launch {
            initializeDistinctId()
            TDAnalytics.enableAutoTrack(
                TDAnalytics.TDAutoTrackEventType.APP_END or TDAnalytics.TDAutoTrackEventType.APP_INSTALL
            )
            Log.d(TAG, "initialize() called with: initialization completed")
        }
    }

    private suspend fun initializeDistinctId() {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        val gaid = verificationUseCase.googleAdId ?: ""
        val androidId = verificationUseCase.androidId ?: ""
        val hasSetDistinctId = verificationUseCase.hasSetDistinctId.first()
        Log.d(
            TAG,
            "initializeDistinctId() called with: gaid = $gaid, androidId = $androidId, hasSetDistinctId = $hasSetDistinctId"
        )

        if (!hasSetDistinctId) {
            val distinctId = when {
                gaid.isNotEmpty() -> gaid
                androidId.isNotEmpty() -> androidId
                else -> verificationUseCase.uuid
            }
            TDAnalytics.setDistinctId(distinctId)
            updateHasSetDistinctId(true)
        }
    }

    private suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) =
        withContext(Dispatchers.IO) {
            val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
            verificationUseCase.updateHasSetDistinctId(hasSetDistinctId)
        }

}