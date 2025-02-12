package com.example.globaldemo.analytic

import android.content.Context
import android.util.Log
import cn.shuzilm.core.Main
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.configuration.ApplicationConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        if (verificationUseCase.smId.first().isNotEmpty()) {
            Log.d(TAG, "queryAndSetId() smId already set")
            return
        }

        // Use withContext to switch to the IO dispatcher for network operations
        withContext(Dispatchers.IO) {
            Main.getQueryID(context, "channel", "message", false) { id ->
                Log.d(TAG, "queryAndSetId() getQueryID callback: id = $id")
                if (id.isNotEmpty()) {
                    // Switch back to the main dispatcher to update the UI or shared state
                    CoroutineScope(Dispatchers.Main).launch {
                        verificationUseCase.setSmId(id)
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
    }
}