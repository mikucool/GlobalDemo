package com.example.globaldemo.data.repository

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.model.AdConfiguration

class AppDataSourceUseCase {
    private val remoteRepository: AppRepository = RemoteAppRepository()
    private val localRepository: AppRepository = LocalAppRepository()
    suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration {
        val isUseLocalData = true
        return if (isUseLocalData) {
            localRepository.fetchAdConfigurationByAdPlatform(adPlatform)
        } else {
            remoteRepository.fetchAdConfigurationByAdPlatform(adPlatform)
        }
    }
}