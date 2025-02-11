package com.example.globaldemo.data

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

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_verification")

class VerificationRepository(private val dataStore: DataStore<Preferences>) {

    val hasSetDistinctId: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAS_SET_DISTINCT_ID] ?: false
        }
    val userId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_ID] ?: ""
    }
    val uuid: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UUID] ?: ""
    }
    val smId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SM_ID] ?: ""
    }
    val adId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AD_ID] ?: ""
    }
    val campaign: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CAMPAIGN] ?: ""
    }
    val campaignId: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CAMPAIGN_ID] ?: ""
    }
    val channel: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CHANNEL] ?: ""
    }

    suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SET_DISTINCT_ID] = hasSetDistinctId
        }
    }

    suspend fun setUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    suspend fun setUuid(uuid: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.UUID] = uuid
        }
    }

    suspend fun setSmId(smId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SM_ID] = smId
        }
    }

    suspend fun setAdId(adId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AD_ID] = adId
        }
    }

    suspend fun setCampaign(campaign: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CAMPAIGN] = campaign
        }
    }

    suspend fun setCampaignId(campaignId: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CAMPAIGN_ID] = campaignId
        }
    }

    suspend fun setChannel(channel: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CHANNEL] = channel
        }
    }

    private object PreferencesKeys {
        val HAS_SET_DISTINCT_ID = booleanPreferencesKey("has_set_distinct_id")
        val USER_ID = stringPreferencesKey("user_id")
        val UUID = stringPreferencesKey("uuid")
        val SM_ID = stringPreferencesKey("sm_id")
        val AD_ID = stringPreferencesKey("ad_id")
        val CAMPAIGN = stringPreferencesKey("campaign")
        val CAMPAIGN_ID = stringPreferencesKey("campaign_id")
        val CHANNEL = stringPreferencesKey("channel")
    }
}