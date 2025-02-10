package com.example.globaldemo.network.interceptor

import android.util.Log
import com.example.globaldemo.model.BaseHttpResult
import com.example.globaldemo.network.RetrofitClient.HTTP_LOG_TAG
import com.example.globaldemo.network.security.HttpSecurityManager
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class DefaultResponseInterceptor : Interceptor {
    companion object {
        private const val TAG = "${HTTP_LOG_TAG}_DefaultResponseInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        val responseBody = response.body
        if (!response.isSuccessful || responseBody == null) return response
        val source = responseBody.source().apply {
            request(Long.MAX_VALUE)
        }
        val buffer = source.buffer
        val serverReplyString = buffer.clone().readString(Charsets.UTF_8)
        if (serverReplyString.isNotEmpty()) {
            val baseHttpResult = Gson().fromJson(serverReplyString, BaseHttpResult::class.java)
            when (baseHttpResult.code) {
                200 -> {
                    val decryptedData =
                        baseHttpResult.data?.let { HttpSecurityManager.aesDecryptResponse(it) }
                    val newBaseHttpResult = baseHttpResult.copy(data = decryptedData)
                    Log.i(TAG, "newBaseHttpResult: $newBaseHttpResult")
                    val newServerReplyString = Gson().toJson(newBaseHttpResult)
                    response =
                        response.newBuilder().body(newServerReplyString.toResponseBody()).build()
                }

                else -> {
                    response =
                        response.newBuilder().body(serverReplyString.toResponseBody()).build()
                }
            }
        }
        return response
    }
}