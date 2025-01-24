package com.example.globaldemo.data

import android.content.Context
import com.example.globaldemo.data.repository.AppDataSourceUseCase
import com.example.globaldemo.data.repository.UserPreferencesUseCase

interface AppContainer {
    val appDataSourceUseCase: AppDataSourceUseCase
    val userPreferencesUseCase: UserPreferencesUseCase
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val appDataSourceUseCase: AppDataSourceUseCase by lazy {
        AppDataSourceUseCase()
    }
    override val userPreferencesUseCase: UserPreferencesUseCase by lazy {
        UserPreferencesUseCase(context)
    }
}