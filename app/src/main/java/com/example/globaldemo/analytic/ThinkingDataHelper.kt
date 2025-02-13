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
import org.json.JSONObject

object ThinkingDataHelper {

    private const val TAG = "dot_info"
    private var hasInitialized = false

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
            initializeDistinctId()  // Initialize distinct ID
            hasInitialized = true
            setUserId() // Set user ID
            TDAnalytics.enableAutoTrack(
                TDAnalytics.TDAutoTrackEventType.APP_END or TDAnalytics.TDAutoTrackEventType.APP_INSTALL
            )   // Enable auto-tracking for app end and install events
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
            verificationUseCase.updateHasSetDistinctId(true)
        }
    }


    private suspend fun setUserId() {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        val userId = verificationUseCase.userId.first()
        if (userId.isNotEmpty()) TDAnalytics.login(userId)
    }

    fun updateSuperProperties(jsonObject: JSONObject) {
        Log.d(TAG, "setSuperProperties() called with: jsonObject = $jsonObject")
        TDAnalytics.setSuperProperties(jsonObject)
    }

    // call on SMId was fetched by SMHelper
    fun updateSMId(smId: String) {
        Log.d(TAG, "updateSMId() called with: smId = $smId")
        if (!hasInitialized) {
            Log.w(TAG, "updateSMId() has not been initialized")
            return
        }
        val jsonObject = JSONObject().apply {
            put("sm_id", smId)
            put("install_vc", BuildConfig.VERSION_CODE)
        }
        userSet(jsonObject)
    }

    fun log(eventKey: String, jsonObject: JSONObject, isForce: Boolean = false) {
        Log.d(TAG, "log() called with: eventKey = $eventKey, jsonObject = $jsonObject, isForce = $isForce")
        if (!hasInitialized) {
            Log.w(TAG, "log() has not been initialized")
            return
        }
        try {
            TDAnalytics.track(eventKey, jsonObject)
            if (isForce) TDAnalytics.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun userSet(jsonObject: JSONObject) {
        Log.d(TAG, "userSet() called with: jsonObject = $jsonObject")
    }

    fun userSetOnce(jsonObject: JSONObject) {
        Log.d(TAG, "userSetOnce() called with: jsonObject = $jsonObject")
        if (!hasInitialized) {
            Log.w(TAG, "userSetOnce() has not been initialized")
            return
        }
        TDAnalytics.userSetOnce(jsonObject)
    }

}