package com.example.globaldemo.domain

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import com.example.globaldemo.data.VerificationRepository
import com.example.globaldemo.data.dataStore
import com.example.globaldemo.model.VerificationInfo
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

@SuppressLint("HardwareIds")
class VerificationUseCase(val context: Context) {
    private val verificationRepository: VerificationRepository =
        VerificationRepository(context.dataStore)
    val hasSetDistinctId: Flow<Boolean> = verificationRepository.hasSetDistinctId
    private val adId: Flow<String> = verificationRepository.adId
    private val campaign: Flow<String> = verificationRepository.campaign
    private val campaignId: Flow<String> = verificationRepository.campaignId
    private val channel: Flow<String> = verificationRepository.channel
    val smId: Flow<String> = verificationRepository.smId

    private val uuid: String by lazy {
        val localUuid = runBlocking { verificationRepository.uuid.first() }
        localUuid.ifEmpty {
            val newUuid = java.util.UUID.randomUUID().toString()
            runBlocking { verificationRepository.setUuid(newUuid) }
            newUuid
        }
    }

    private val androidId: String? by lazy {
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    private val googleAdId: String? by lazy {
        try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
            info.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val timeZone: String? by lazy {
        try {
            java.util.TimeZone.getDefault().id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val language: String? by lazy {
        context.resources.configuration.locales.get(0)?.language
    }

    private val country: String by lazy {
        try {
            var countryCode = context.resources.configuration.locales[0].country
            if (countryCode.isEmpty()) {
                countryCode = Locale.getDefault().country
            }
            countryCode
        } catch (e: Exception) {
            e.printStackTrace()
            "UNKNOWN"
        }
    }

    private val sim: Int by lazy {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simOperatorName = tm.simOperatorName
            if (simOperatorName.isNullOrEmpty()) 1 else 0
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            -1
        }
    }

    private val userType: Int = 0

    suspend fun updateHasSetDistinctId(hasSetDistinctId: Boolean) {
        verificationRepository.updateHasSetDistinctId(hasSetDistinctId)
    }

    suspend fun setUserId(userId: String) {
        verificationRepository.setUserId(userId)
    }

    suspend fun setAdId(adId: String) {
        verificationRepository.setAdId(adId)
    }

    suspend fun setCampaign(campaign: String) {
        verificationRepository.setCampaign(campaign)
    }

    suspend fun setCampaignId(campaignId: String) {
        verificationRepository.setCampaignId(campaignId)
    }

    suspend fun setChannel(channel: String) {
        verificationRepository.setChannel(channel)
    }

    suspend fun setUuid(uuid: String) {
        verificationRepository.setUuid(uuid)
    }

    suspend fun setSmId(smId: String) {
        verificationRepository.setSmId(smId)
    }

    suspend fun getVerificationInfo(): VerificationInfo {
        return VerificationInfo(
            uuid = uuid,
            androidId = androidId,
            userType = userType,
            googleAdId = googleAdId,
            timeZone = timeZone,
            language = language,
            sim = sim,
            country = country,
            adId = adId.first(),
            channel = channel.first(),
            campaign = campaign.first(),
            campaignId = campaignId.first(),
            thirdId = smId.first()
        )
    }
}