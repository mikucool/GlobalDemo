package com.example.globaldemo.ad

import android.content.Context
import android.util.Log
import com.kwai.network.sdk.KwaiAdSDK
import com.kwai.network.sdk.api.KwaiInitCallback
import com.kwai.network.sdk.api.SdkConfig

class AdUseCase {
    companion object {
        const val TAG = "AdUseCase"
        fun initKwaiAd(context: Context) {
            KwaiAdSDK.init(
                context, SdkConfig.Builder()
                    .appId("899999")
                    .token("EaCw0AipSYyvf3E7")
                    .debug(true)
                    .setInitCallback(object : KwaiInitCallback {
                        override fun onSuccess() {
                            Log.d(TAG, "onSuccess() called")
                        }

                        override fun onFail(p0: Int, p1: String?) {
                            Log.d(TAG, "onFail() called with: p0 = $p0, p1 = $p1")
                        }
                    })
                    .build()
            )
        }
    }
}