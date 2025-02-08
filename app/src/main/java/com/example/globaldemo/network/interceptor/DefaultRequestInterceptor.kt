package com.example.globaldemo.network.interceptor

import android.util.Log
import com.example.globaldemo.model.DefaultHttpHeader
import com.example.globaldemo.network.security.HttpSecurityManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class DefaultRequestInterceptor : Interceptor {
    companion object {
        private const val TAG = "HttpHeaderInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d(TAG, "intercept() called with: chain = $chain, is request body null: ${chain.request().body == null}")
        val originalRequest = chain.request()
        if (originalRequest.body == null) return chain.proceed(originalRequest)
        val defaultHttpHeader = getDefaultHttpHeader()
        Log.i(TAG, "<-----------------before encrypt request----------------->")
        Log.i(TAG, "default header: $defaultHttpHeader")
        val originalRequestBodyBuffer = Buffer()
        originalRequest.body?.writeTo(originalRequestBodyBuffer)
        val originalRequestString = originalRequestBodyBuffer.readString(Charsets.UTF_8)
        Log.i(TAG, "original request body: $originalRequestString")
        Log.i(TAG, "<-----------------before encrypt request----------------->")
        val encryptedRequest = HttpSecurityManager.encryptRequest(
            originalRequest = originalRequest,
            defaultHttpHeader = defaultHttpHeader
        )

        Log.i(TAG, "<-----------------after encrypt request----------------->")
        val encryptedHeaders = encryptedRequest.headers
        encryptedHeaders.forEach {
            Log.i(TAG, "encrypted header: ${it.first} = ${it.second}")
        }
        Log.i(TAG, "encrypted body: ${encryptedRequest.body}")
        Log.i(TAG, "<-----------------after encrypt request----------------->")
        return chain.proceed(encryptedRequest)
    }

    private fun getDefaultHttpHeader(): DefaultHttpHeader {
        return DefaultHttpHeader(
            adid = "",
            androidId = "",
            androidVersion = "",
            appid = "",
            campaign = "",
            campaignId = "",
            channel = "",
            country = "",
            currentTime = 0L,
            gaid = "",
            language = "",
            packageName = "",
            phoneBrand = "",
            phoneModel = "",
            shumengPkgName = "",
            sim = 0,
            thirdId = "",
            timeZone = "",
            token = "",
            userType = "",
            uuid = "",
            vc = "",
            vn = ""
        )
    }
}