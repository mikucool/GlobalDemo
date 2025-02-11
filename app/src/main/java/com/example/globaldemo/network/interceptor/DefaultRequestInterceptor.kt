package com.example.globaldemo.network.interceptor

import android.os.Build
import android.util.Log
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.network.RetrofitClient.HTTP_LOG_TAG
import com.example.globaldemo.network.security.HttpSecurityManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID

class DefaultRequestInterceptor : Interceptor {

    companion object {
        private const val TAG = "${HTTP_LOG_TAG}_DefaultRequestInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (originalRequest.body == null) return chain.proceed(originalRequest)
        val defaultHttpHeader = runBlocking { getUpdatedDefaultHttpHeader() }
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

    private suspend fun getUpdatedDefaultHttpHeader(): DefaultHttpHeader {
        val verificationInfo = container.verificationUseCase.getVerificationInfo()
        val randomToken = UUID.randomUUID().toString()
        return DefaultHttpHeader(
            currentTime = System.currentTimeMillis(),
            uuid = verificationInfo.uuid,
            androidId = verificationInfo.androidId,
            userType = verificationInfo.userType,
            gaid = verificationInfo.googleAdId,
            timeZone = verificationInfo.timeZone,
            language = verificationInfo.language,
            sim = verificationInfo.sim,
            country = verificationInfo.country,
            token = randomToken,
            adid = verificationInfo.adId,
            channel = verificationInfo.channel,
            campaign = verificationInfo.campaign,
            campaignId = verificationInfo.campaignId,
            thirdId = verificationInfo.thirdId
        )
    }

    data class DefaultHttpHeader(
        val adid: String? = null,
        val androidId: String? = null,
        val androidVersion: Int = Build.VERSION.SDK_INT,
        val appid: String = ApplicationConfiguration.APP_ID,
        val campaign: String? = null,
        val campaignId: String? = null,
        val channel: String? = null,
        val country: String? = null,
        val currentTime: Long = 0,
        val gaid: String? = null,
        val language: String? = "",
        val packageName: String = ApplicationConfiguration.APP_VEST_PACKAGE_NAME,
        val phoneBrand: String = Build.BRAND,
        val phoneModel: String = Build.MODEL,
        val shumengPkgName: String = BuildConfig.APPLICATION_ID,
        val sim: Int = -1,
        val thirdId: String? = null,
        val timeZone: String? = "",
        val token: String? = null,
        val userType: Int = 0,
        val uuid: String? = null,
        val vc: Int = BuildConfig.VERSION_CODE,
        val vn: String = BuildConfig.VERSION_NAME
    )

}