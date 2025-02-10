package com.example.globaldemo.network.interceptor

import android.util.Log
import com.example.globaldemo.model.DefaultHttpHeader
import com.example.globaldemo.network.RetrofitClient.HTTP_LOG_TAG
import com.example.globaldemo.network.security.HttpSecurityManager
import com.example.globaldemo.verification.VerificationHelper
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class DefaultRequestInterceptor : Interceptor {

    private val defaultHttpHeader: DefaultHttpHeader by lazy { DefaultHttpHeader() }

    companion object {
        private const val TAG = "${HTTP_LOG_TAG}_DefaultRequestInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.body == null) return chain.proceed(originalRequest)
        val defaultHttpHeader = getUpdatedDefaultHttpHeader()
        Log.i(TAG, "<-----------------encrypt request start----------------->")
        Log.i(TAG, "default header: $defaultHttpHeader")
        val originalRequestBodyBuffer = Buffer()
        originalRequest.body?.writeTo(originalRequestBodyBuffer)
        val originalRequestString = originalRequestBodyBuffer.readString(Charsets.UTF_8)
        Log.i(TAG, "original request body: $originalRequestString")
        Log.i(TAG, "<-----------------encrypt request end----------------->")
        val encryptedRequest = HttpSecurityManager.encryptRequest(
            originalRequest = originalRequest,
            defaultHttpHeader = defaultHttpHeader
        )

        Log.i(TAG, "<-----------------encrypt request start----------------->")
        val encryptedHeaders = encryptedRequest.headers
        encryptedHeaders.forEach {
            Log.i(TAG, "encrypted header: ${it.first} = ${it.second}")
        }
        Log.i(TAG, "encrypted body: ${encryptedRequest.body}")
        Log.i(TAG, "<-----------------encrypt request end----------------->")
        return chain.proceed(encryptedRequest)
    }

    private fun getUpdatedDefaultHttpHeader(): DefaultHttpHeader {
        return this.defaultHttpHeader.copy(
            currentTime = System.currentTimeMillis(),
            uuid = VerificationHelper.getUUID(),
            androidId = VerificationHelper.getAndroidId(),
            userType = VerificationHelper.getUerType(),
            gaid = VerificationHelper.getGoogleAdId(),
            timeZone = VerificationHelper.getTimeZone(),
            language = VerificationHelper.getLanguage(),
            sim = VerificationHelper.getSim(),
            country = VerificationHelper.getCountry(),
            token = VerificationHelper.getRandomToken()
        )
    }

}