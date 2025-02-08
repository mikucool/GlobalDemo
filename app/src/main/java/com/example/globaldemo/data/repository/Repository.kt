package com.example.globaldemo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.network.GlobalDemoService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AppRepository {
    suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration
}

class RemoteAppRepository(private val globalDemoService: GlobalDemoService) : AppRepository {
    suspend fun testGet(): Any = globalDemoService.testGet()
    suspend fun testPost(body: String): Any = globalDemoService.testPost(body)

    override suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration {
        return AdConfiguration()
    }

}

class LocalAppRepository : AppRepository {
    override suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration =
        LocalDataProvider.fetchAdConfigurationByAdPlatform(adPlatform)

}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    val isDarkMode: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] ?: false
        }
    val customId: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOM_ID] ?: ""
        }
    val hasSetDistinctId: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SET_DISTINCT_ID] ?: false
        }

    suspend fun updateCustomId(customId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_ID] = customId
        }
    }

    suspend fun updateDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DARK_MODE] = isDarkMode
        }
    }

    suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SET_DISTINCT_ID] = hasSetDistinctId
        }
    }

    private object PreferencesKeys {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val CUSTOM_ID = stringPreferencesKey("custom_id")
        val HAS_SET_DISTINCT_ID = booleanPreferencesKey("has_set_distinct_id")
    }
}