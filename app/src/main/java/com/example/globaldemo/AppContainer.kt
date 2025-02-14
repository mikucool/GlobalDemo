package com.example.globaldemo

import android.content.Context
import com.example.globaldemo.ad.AdManager
import com.example.globaldemo.domain.AppDataSourceUseCase
import com.example.globaldemo.domain.VerificationUseCase

interface AppContainer {
    val appDataSourceUseCase: AppDataSourceUseCase
    val verificationUseCase: VerificationUseCase
    val adManager: AdManager
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val appDataSourceUseCase: AppDataSourceUseCase by lazy {
        AppDataSourceUseCase()
    }
    override val verificationUseCase: VerificationUseCase by lazy {
        VerificationUseCase(context)
    }

    override val adManager: AdManager by lazy {
        AdManager()
    }

}