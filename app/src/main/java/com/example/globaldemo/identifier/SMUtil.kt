package com.example.globaldemo.identifier

import android.content.Context
import android.util.Log
import cn.shuzilm.core.Main
import com.example.globaldemo.configuration.ApplicationConfiguration

/**
 * 数盟工具类
 */
object SMUtil {
    private const val TAG = "SMUtil"
    fun initSM(context: Context) {
        Main.init(context, ApplicationConfiguration.SM_API_KEY, false)
        Main.getQueryID(context, "channel", "message", false) { p0 ->
            Log.d(TAG, "handler() called with: p0 = $p0")
        }
    }
}