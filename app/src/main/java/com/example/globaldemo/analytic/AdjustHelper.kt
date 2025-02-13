package com.example.globaldemo.analytic

import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAttribution
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.adjust.sdk.OnAttributionChangedListener
import com.example.globaldemo.BuildConfig
import com.example.globaldemo.GlobalDemoApplication
import com.example.globaldemo.configuration.ApplicationConfiguration
import com.example.globaldemo.model.AdjustInitConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.regex.Pattern
import kotlin.random.Random
import kotlin.reflect.KClass

object AdjustHelper {
    private const val TAG = "AdjustInitializer"
    private var startTime = 0L

    fun startAttribute(context: Context, triggerType: KClass<out AdjustInitConfiguration>) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "startAttribute() called with: context = $context")
            if (isNeedToAttribute() && checkAdjustInitConfig(triggerType)) {
                Log.d(TAG, "startAttribute()...")
                val adjustEnvironment =
                    if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
                val adjustLogLevel = if (BuildConfig.DEBUG) LogLevel.VERBOSE else LogLevel.WARN
                val adjustConfig = AdjustConfig(
                    context,
                    ApplicationConfiguration.ADJUST_APP_TOKEN,
                    adjustEnvironment
                ).apply {
                    setLogLevel(adjustLogLevel)
                    enableSendingInBackground()
                    onAttributionChangedListener = OnAttributionChangedListener {
                        Log.d(TAG, "onAttributionChanged() called with: it = $it")
                        CoroutineScope(Dispatchers.IO).launch {
                            onAttributeChanged(it)
                        }
                    }
                }
                startTime = System.currentTimeMillis()
                Adjust.initSdk(adjustConfig)
                updateAdIdIfNeeded()
                // TODO: Adjust resume logic
            }
        }
    }

    private suspend fun updateAdIdIfNeeded() {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        if (verificationUseCase.adId.first().isNotEmpty()) return
        Adjust.getAdid {
            CoroutineScope(Dispatchers.IO).launch {
                verificationUseCase.setAdId(it)
            }
        }
    }

    private suspend fun isNeedToAttribute(): Boolean {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        val hasSIM = verificationUseCase.sim == 0
        val isChina = verificationUseCase.country.equals("CN", ignoreCase = true)
                || verificationUseCase.country.equals("CHN", ignoreCase = true)
        val isAttributed = verificationUseCase.channel.first().isNotEmpty()
        return hasSIM && !isChina && !isAttributed
    }

    private suspend fun checkAdjustInitConfig(triggerType: KClass<out AdjustInitConfiguration>): Boolean {
        val appDataSourceUseCase = GlobalDemoApplication.container.appDataSourceUseCase
        val adUseCase = GlobalDemoApplication.container.adUseCase
        val adjustInitConfig = appDataSourceUseCase.fetchAdjustInitConfig()
        Log.d(
            TAG,
            "checkAdjustInitConfig() called with: triggerType = $triggerType, configClass: ${adjustInitConfig.javaClass}"
        )
        if (adjustInitConfig.javaClass != triggerType) return false
        return when (adjustInitConfig) {
            is AdjustInitConfiguration.None -> false

            is AdjustInitConfiguration.OnAppStart, is AdjustInitConfiguration.OnSmallWithdrawalTaskCompletion,
            is AdjustInitConfiguration.OnWithdrawalScreenAfterTask, is AdjustInitConfiguration.OnWithdrawalButtonClickAfterTask,
            is AdjustInitConfiguration.OnWithdrawalInitiated -> {
                checkProbability(adjustInitConfig.initProbability)
            }

            is AdjustInitConfiguration.AfterSpecificAdViews -> {
                adUseCase.videoAdShowTimes > adjustInitConfig.adViewCount
            }
        }
    }

    private fun checkProbability(probability: Float): Boolean {
        val randomValue = Random.nextFloat()
        val result = randomValue < probability
        Log.d(
            TAG,
            "checkProbability() called with: probability = $probability, randomValue = $randomValue, result = $result"
        )
        return result
    }

    private suspend fun onAttributeChanged(adjustAttribution: AdjustAttribution) {
        val verificationUseCase = GlobalDemoApplication.container.verificationUseCase
        val channel = adjustAttribution.network?.trim().orEmpty()
        if (channel.isNotEmpty()) {
            verificationUseCase.setChannel(channel)
        }

        var campaign = adjustAttribution.campaign?.trim().orEmpty()
        var campaignId = ""
        if (campaign.isNotEmpty()) {
            if (campaign.contains("(") && campaign.endsWith(")")) {
                val p = Pattern.compile("\\((.*?)\\)")
                val str = p.split(campaign)
                val matcher = p.matcher(campaign)
                if (matcher.find()) {
                    campaignId = matcher.group(1)?.trim().orEmpty()
                }
                if (str.isNotEmpty()) {
                    campaign = str[0].trim()
                }
                if (campaign.isNotEmpty()) {
                    verificationUseCase.setCampaign(campaign)
                }
                if (campaignId.isNotEmpty()) {
                    verificationUseCase.setCampaignId(campaignId)
                }
            } else {
                verificationUseCase.setCampaign(campaign)
            }
        }
        reportToThinkingData(
            channel = channel,
            campaign = campaign,
            campaignId = campaignId,
            adGroup = adjustAttribution.adgroup?.trim().orEmpty(),
            creative = adjustAttribution.creative?.trim().orEmpty()
        )
    }

    private fun reportToThinkingData(
        channel: String = "",
        campaign: String = "",
        campaignId: String = "",
        adGroup: String = "",
        creative: String = ""
    ) {
        ThinkingDataHelper.updateSuperProperties(JSONObject().apply {
            put("channel", channel)
            put("campaign", campaign)
            put("vc", BuildConfig.VERSION_CODE)
            put("coins", LocalUserDataHelper.getUserCoins())
            put("banknotes", LocalUserDataHelper.getUserMoney())
        })

        ThinkingDataHelper.userSetOnce(JSONObject().apply {
            put("channel", channel)
            put("campaign", campaign)
            put("campaignid", campaignId)
            put("adgroup", adGroup)
            put("creative", creative)
            // TODO: fetch server_country
            put("server_country", "not yet implemented")
            put("vc", BuildConfig.VERSION_CODE)
        })

        ThinkingDataHelper.userSet(JSONObject().apply {
            put("coins", LocalUserDataHelper.getUserCoins())
            put("banknotes", LocalUserDataHelper.getUserMoney())
        })
        ThinkingDataHelper.log("tenji_time", JSONObject().apply {
            put("time", System.currentTimeMillis() - startTime)
        })
    }
}