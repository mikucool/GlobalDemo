package com.example.globaldemo.analytic

import android.content.Context
import android.util.Log
import cn.shuzilm.core.Main
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.configuration.ApplicationConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

object SMHelper {

    private const val TAG = "SMInitializer"

    fun initialize(context: Context) {
        Log.d(TAG, "initialize() called")
        CoroutineScope(Dispatchers.IO).launch {
            initializeSM(context)
            queryAndSetId(context)
        }
    }

    private fun initializeSM(context: Context) {
        Log.d(TAG, "initializeSM() called")
        Main.init(context, ApplicationConfiguration.SM_API_KEY, false)
    }

    private suspend fun queryAndSetId(context: Context) {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        Log.d(TAG, "queryAndSetId() called")
        // Check if smId is already set
        val smId = verificationUseCase.smId.first()
        if (smId.isNotEmpty()) {
            Log.d(TAG, "queryAndSetId() smId already set")
            reportToThinkingData(smId)
            return
        }

        Main.getQueryID(context, "channel", "message", false) { id ->
            Log.d(TAG, "queryAndSetId() getQueryID callback: id = $id")
            if (id.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    verificationUseCase.setSmId(id)
                    reportToThinkingData(smId)
                    Log.d(TAG, "queryAndSetId() smId set successfully")
                }
            } else {
                Log.w(TAG, "queryAndSetId() getQueryID returned empty id, retrying...")
                // Retry recursively (consider adding a retry limit)
                CoroutineScope(Dispatchers.IO).launch {
                    queryAndSetId(context)
                }
            }
        }
    }

    private fun reportToThinkingData(smId: String) {
        val jsonObject = JSONObject().apply {
            put("sm_id", smId)
            put("install_vc", BuildConfig.VERSION_CODE)
        }
        ThinkingDataHelper.userSet(jsonObject)
    }
}