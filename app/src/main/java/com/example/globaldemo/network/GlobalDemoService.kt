package com.example.globaldemo.network

import com.example.globaldemo.model.BaseFpResult
import com.example.globaldemo.model.BaseHttpResult
import com.example.globaldemo.model.FpParameters
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GlobalDemoService {
    @GET("/get")
    suspend fun testGet(): Any

    @POST("/post")
    suspend fun testPost(@Body body: String): Any

    @POST("/fp")
    suspend fun fetchAppFp(@Body fpParameters: FpParameters): BaseHttpResult<BaseFpResult>

}