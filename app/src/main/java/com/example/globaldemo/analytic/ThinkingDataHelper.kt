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
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 在设置setDistinctId之前，所有的打点，和用户属性上报事件都要添加到自定义队列中，等设置完setDistinctId之后再统一上报
 */
object ThinkingDataHelper {

    private const val TAG = "dot_info"
    private var hasInitialized = false
    private val eventQueue = ConcurrentLinkedQueue<Runnable>() // Use a thread-safe queue

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
            initializeDistinctId()  // Initialize distinct ID,
            hasInitialized = true
            setUserId() // Set user ID
            TDAnalytics.enableAutoTrack(
                TDAnalytics.TDAutoTrackEventType.APP_END or TDAnalytics.TDAutoTrackEventType.APP_INSTALL
            )   // Enable auto-tracking for app end and install events
            Log.d(TAG, "initialize() called with: initialization completed")
            reportCacheEvents()
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

    private fun reportCacheEvents() {
        Log.d(TAG, "reportCacheEvents() called")
        while (true) {
            val event = eventQueue.poll() ?: break // Exit loop when queue is empty
            event.run()
        }
    }

    private fun enqueueOrRun(block: () -> Unit) {
        if (hasInitialized) {
            block()
        } else {
            eventQueue.offer(Runnable { block() })
            Log.w(TAG, "Event enqueued because SDK is not initialized yet.")
        }
    }


    private suspend fun setUserId() {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        val userId = verificationUseCase.userId.first()
        if (userId.isNotEmpty()) TDAnalytics.login(userId)
    }

    fun updateSuperProperties(jsonObject: JSONObject) {
        enqueueOrRun {
            Log.d(TAG, "setSuperProperties() called with: jsonObject = $jsonObject")
            TDAnalytics.setSuperProperties(jsonObject)
        }
    }

    fun log(eventKey: String, jsonObject: JSONObject, isForce: Boolean = false) {
        enqueueOrRun {
            try {
                Log.d(
                    TAG,
                    "log() called with: eventKey = $eventKey, jsonObject = $jsonObject, isForce = $isForce"
                )
                TDAnalytics.track(eventKey, jsonObject)
                if (isForce) TDAnalytics.flush()
            } catch (e: Exception) {
                Log.e(TAG, "Error logging event", e)
            }
        }
    }

    fun userSet(jsonObject: JSONObject) {
        enqueueOrRun {
            Log.d(TAG, "userSet() called with: jsonObject = $jsonObject")
            TDAnalytics.userSet(jsonObject)
        }
    }

    fun userSetOnce(jsonObject: JSONObject) {
        enqueueOrRun {
            Log.d(TAG, "userSetOnce() called with: jsonObject = $jsonObject")
            TDAnalytics.userSetOnce(jsonObject)
        }
    }

}