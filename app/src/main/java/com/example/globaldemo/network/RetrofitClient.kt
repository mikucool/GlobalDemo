package com.example.globaldemo.network

import com.example.globaldemo.network.interceptor.DefaultRequestInterceptor
import com.example.globaldemo.network.interceptor.DefaultResponseInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.50.32:8080" // Replace with your API base URL
    const val HTTP_LOG_TAG = "HttpLog"
    val globalDemoService: GlobalDemoService by lazy {

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(DefaultRequestInterceptor())
            .addInterceptor(DefaultResponseInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .client(okHttpClient)
            .build()

        retrofit.create(GlobalDemoService::class.java)
    }
}