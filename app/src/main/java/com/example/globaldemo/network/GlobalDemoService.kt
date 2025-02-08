package com.example.globaldemo.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GlobalDemoService {
    @GET("/get")
    suspend fun testGet(): Any

    @POST("/post")
    suspend fun testPost(@Body body: String): Any

}