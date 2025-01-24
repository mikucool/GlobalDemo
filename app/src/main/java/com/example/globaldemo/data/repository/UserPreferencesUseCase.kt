package com.example.globaldemo.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow

class UserPreferencesUseCase(context: Context) {
    private val userPreferencesRepository: UserPreferencesRepository =
        UserPreferencesRepository(context.dataStore)
    val isDarkMode: Flow<Boolean> = userPreferencesRepository.isDarkMode
    val customId: Flow<String> = userPreferencesRepository.customId
    val hasSetDistinctId: Flow<Boolean> = userPreferencesRepository.hasSetDistinctId

    suspend fun updateCustomId(customId: String) {
        userPreferencesRepository.updateCustomId(customId)
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        userPreferencesRepository.updateDarkMode(isDarkMode)
    }
    suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) {
        userPreferencesRepository.updateHasSetDistinctId(hasSetDistinctId)
    }

}