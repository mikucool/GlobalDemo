package com.example.globaldemo.data

import android.content.Context

interface AppContainer {
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.dataStore)
    }
}