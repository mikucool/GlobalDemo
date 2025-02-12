package com.example.globaldemo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.configuration.FpConfigurationField
import com.example.globaldemo.configuration.FpConfigurationId
import com.example.globaldemo.model.AdConfiguration
import com.example.globaldemo.model.AdjustInitConfiguration
import com.example.globaldemo.model.BaseFpResult
import com.example.globaldemo.model.FpConfiguration
import com.example.globaldemo.model.FpParameters
import com.example.globaldemo.network.GlobalDemoService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AppRepository {
    suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration
    suspend fun fetchAdjustInitConfig(): AdjustInitConfiguration
}

class RemoteAppRepository(private val globalDemoService: GlobalDemoService) : AppRepository {
    suspend fun testGet(): Any = globalDemoService.testGet()
    suspend fun testPost(body: String): Any = globalDemoService.testPost(body)

    override suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration {
        return AdConfiguration()
    }

    override suspend fun fetchAdjustInitConfig(): AdjustInitConfiguration {
        val adjustFp = globalDemoService.fetchAppFp(getAppFpParameters()).data
        val adjustConfiguration =
            adjustFp?.findFpConfigurationByField(FpConfigurationField.FLYING_CHESS_ADJUST_INIT_CONFIGURATION)
        return if (adjustConfiguration != null) {
            val configJson = adjustConfiguration.jsonContent
            val stringArray = Gson().fromJson(configJson, Array<String>::class.java)
            val first = stringArray[0].toInt()
            val second = stringArray[1].toFloat()
            when (first) {
                0 -> AdjustInitConfiguration.None()
                1 -> AdjustInitConfiguration.OnAppStart(second)
                2 -> AdjustInitConfiguration.OnSmallWithdrawalTaskCompletion(second)
                3 -> AdjustInitConfiguration.OnWithdrawalScreenAfterTask(second)
                4 -> AdjustInitConfiguration.OnWithdrawalButtonClickAfterTask(second)
                5 -> AdjustInitConfiguration.OnWithdrawalInitiated(second)
                else -> {
                    if (first > 10)
                        AdjustInitConfiguration.AfterSpecificAdViews(first - 10, second)
                    else AdjustInitConfiguration.None()
                }
            }
        } else {
            AdjustInitConfiguration.None()
        }
    }

    private fun getAppFpParameters(): FpParameters {
        return FpParameters(
            fpConfigId = FpConfigurationId.FLYING_CHESS_APP_CONFIGURATION,
            configField = "${FpConfigurationField.FLYING_CHESS_ADJUST_INIT_CONFIGURATION}," +
                    "${FpConfigurationField.FLYING_CHESS_AD_MAX_INTERSTITIAL_IDS}," +
                    "${FpConfigurationField.FLYING_CHESS_AD_MAX_REWARD_IDS}," +
                    "${FpConfigurationField.FLYING_CHESS_AD_KWAI_REWARD_IDS}," +
                    "${FpConfigurationField.FLYING_CHESS_AD_BIGO_REWARD_IDS},"
        )
    }

    private fun BaseFpResult.findFpConfigurationByField(field: String): FpConfiguration? {
        return this.fpConfigurations.find { it.id == field }
    }

}

class LocalAppRepository : AppRepository {
    override suspend fun fetchAdConfigurationByAdPlatform(adPlatform: AdPlatform): AdConfiguration =
        LocalDataProvider.fetchAdConfigurationByAdPlatform(adPlatform)

    override suspend fun fetchAdjustInitConfig(): AdjustInitConfiguration =
        LocalDataProvider.fetchAdjustInitConfig()

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