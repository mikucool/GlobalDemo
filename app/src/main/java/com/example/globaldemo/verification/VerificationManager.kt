package com.example.globaldemo.verification

import android.content.Context
import android.util.Log
import cn.shuzilm.core.Main
import com.example.globaldemo.configuration.ApplicationConfiguration

object VerificationManager {
    private const val TAG = "VerificationManager"
    fun initSMAndQueryID(context: Context) {
        Main.init(context, ApplicationConfiguration.SM_API_KEY, false)
        Main.getQueryID(context, "channel", "message", false) { p0 ->
            Log.d(TAG, "handler() called with: p0 = $p0")
        }
    }
}