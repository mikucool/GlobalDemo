package com.example.globaldemo.network.interceptor

import android.text.TextUtils
import android.util.Log
import com.example.globaldemo.model.BackBaseHttpResult
import com.example.globaldemo.network.RetrofitClient.HTTP_LOG_TAG
import com.example.globaldemo.network.security.HttpSecurityManager
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class DefaultResponseInterceptor : Interceptor {
    companion object {
        private const val TAG = "${HTTP_LOG_TAG}_DefaultResponseInterceptor"
        private const val SUCCESS_CODE = 200
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
            val baseHttpResult = Gson().fromJson(serverReplyString, BackBaseHttpResult::class.java)
            when (baseHttpResult.code) {
                SUCCESS_CODE -> {
                    val decryptedData = baseHttpResult.data?.let {
                        val tempDecryptedData = HttpSecurityManager.aesDecryptResponse(it)
                        // remove ??? that behind the data
                        JSONTokener(tempDecryptedData).nextValue().toString()
                    }
                    val newBaseHttpResult = baseHttpResult.copy(data = decryptedData)
                    Log.d(TAG, "intercept() called with: newBaseHttpResult = $newBaseHttpResult")
                    val resultObject = createResultObject(newBaseHttpResult)
                    Log.d(TAG, "intercept() called with: resultObject = $resultObject")
                    response = response.newBuilder().body(resultObject.toString().toResponseBody()).build()
                }

                else -> {
                    response =
                        response.newBuilder().body(serverReplyString.toResponseBody()).build()
                }
            }
        }
        return response
    }

    private fun createResultObject(baseHttpResult: BackBaseHttpResult): JSONObject {
        val jsonObject = JSONObject(Gson().toJson(baseHttpResult))
        val resultObject = JSONObject().apply {
            put("code", baseHttpResult.code)
            put("msg", baseHttpResult.msg)
        }
        val data = if (jsonObject.has("data")) jsonObject.getString("data") else ""
        resultObject.put("data", when {
            TextUtils.isEmpty(data) -> null
            data.startsWith("[") -> JSONArray(data)
            else -> JSONObject(data)
        })
        return resultObject
    }
}