package com.example.globaldemo.domain

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.data.AppRepository
import com.example.globaldemo.data.LocalAppRepository
import com.example.globaldemo.data.RemoteAppRepository
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.network.RetrofitClient

class AppDataSourceUseCase {
    private val remoteRepository: AppRepository =
        RemoteAppRepository(RetrofitClient.globalDemoService)
    private val localRepository: AppRepository = LocalAppRepository()
    suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration {
        val isUseLocalData = true
        return if (isUseLocalData) {
            localRepository.fetchAdConfigurationByAdPlatform(adPlatform)
        } else {
            remoteRepository.fetchAdConfigurationByAdPlatform(adPlatform)
        }
    }

    suspend fun testGet(): Any {
        return (remoteRepository as RemoteAppRepository).testGet()
    }

    suspend fun testPost(body: String): Any {
        return (remoteRepository as RemoteAppRepository).testPost(body)
    }

}