package com.example.globaldemo.analytic

import android.content.Context
import android.util.Log
import cn.thinkingdata.analytics.TDAnalytics
import cn.thinkingdata.analytics.TDConfig
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.domain.VerificationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Utility class for initializing and managing ThinkingData analytics.
 */
class ThinkingDataInitializer(private val verificationUseCase: VerificationUseCase) {


    companion object {
        private const val TAG = "ThinkingDataAnalyticsManager"
    }

    /**
     * Initializes ThinkingData analytics.
     *
     * @param context The application context.
     */
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
            enableAutoTrack()
            Log.d(TAG, "initialize() called with: initialization completed")
        }
    }

    /**
     * Initializes the distinct ID for ThinkingData analytics.
     *
     * This function retrieves the GAID, Android ID, and checks if a distinct ID has already been set.
     * If no distinct ID is set, it sets one based on the available identifiers (GAID, Android ID, or UUID).
     */
    private suspend fun initializeDistinctId() {
        val gaid = getGaid()
        val androidId = getAndroidId()
        val hasSetDistinctId = hasSetDistinctId()

        Log.d(
            TAG,
            "initializeDistinctId() called with: gaid = $gaid, androidId = $androidId, hasSetDistinctId = $hasSetDistinctId"
        )

        if (!hasSetDistinctId) {
            val distinctId = when {
                gaid.isNotEmpty() -> gaid
                androidId.isNotEmpty() -> androidId
                else -> getUUID()
            }
            setDistinctId(distinctId)
            updateHasSetDistinctId(true)
        }
    }

    /**
     * Enables automatic tracking of app events.
     */
    private fun enableAutoTrack() {
        TDAnalytics.enableAutoTrack(
            TDAnalytics.TDAutoTrackEventType.APP_END or TDAnalytics.TDAutoTrackEventType.APP_INSTALL
        )
    }

    /**
     * Checks if a distinct ID has already been set.
     *
     * @return `true` if a distinct ID has been set, `false` otherwise.
     */
    private suspend fun hasSetDistinctId(): Boolean = withContext(Dispatchers.IO) {
        verificationUseCase.hasSetDistinctId.first()
    }

    /**
     * Updates the flag indicating whether a distinct ID has been set.
     *
     * @param hasSetDistinctId `true` if a distinct ID has been set, `false` otherwise.
     */
    private suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) =
        withContext(Dispatchers.IO) {
        verificationUseCase.updateHasSetDistinctId(hasSetDistinctId)
    }

    /**
     * Retrieves the Google Advertising ID (GAID).
     *
     * @return The GAID or an empty string if not available.
     */
    private fun getGaid(): String {
        return verificationUseCase.googleAdId ?: ""
    }

    /**
     * Retrieves the Android ID.
     *
     * @return The Android ID or an empty string if not available.
     */
    private fun getAndroidId(): String {
        return verificationUseCase.androidId ?: ""
    }

    /**
     * Generates a UUID.
     *
     * @return A new UUID.
     */
    private fun getUUID(): String {
        return verificationUseCase.uuid
    }

    /**
     * Sets the distinct ID for ThinkingData analytics.
     *
     * @param distinctId The distinct ID to set.
     */
    private fun setDistinctId(distinctId: String) {
        TDAnalytics.setDistinctId(distinctId)
        Log.d(TAG, "setDistinctId() called with: distinctId = $distinctId")
    }
}