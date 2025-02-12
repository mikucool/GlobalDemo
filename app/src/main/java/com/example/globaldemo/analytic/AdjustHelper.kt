package com.example.globaldemo.analytic

import android.content.Context
import android.text.TextUtils
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
import java.util.regex.Pattern
import kotlin.random.Random
import kotlin.reflect.KClass

object AdjustHelper {
    private const val TAG = "AdjustInitializer"

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
                Adjust.initSdk(adjustConfig)
                updateAdIdIfNeeded()
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
        val channel = adjustAttribution.network?.trim() ?: ""
        val adjustAttributionMap = mutableMapOf<String, String>()
        if (channel.isNotEmpty()) {
            adjustAttributionMap["channel"] = channel
            verificationUseCase.setChannel(channel)
        }

        var campaign = adjustAttribution.campaign?.trim() ?: ""
        if (campaign.isNotEmpty()) {
            if (campaign.contains("(") && campaign.endsWith(")")) {
                var campaignId = ""
                val p = Pattern.compile("\\((.*?)\\)")
                val str = p.split(campaign)
                val matcher = p.matcher(campaign)
                if (matcher.find()) {
                    campaignId = matcher.group(1)?.trim() ?: ""
                }
                if (str.isNotEmpty()) {
                    campaign = str[0].trim()
                }
                if (campaign.isNotEmpty()) {
                    adjustAttributionMap["campaign"] = campaign
                    verificationUseCase.setCampaign(campaign)
                }
                if (campaignId.isNotEmpty()) {
                    adjustAttributionMap["campaignid"] = campaignId
                    verificationUseCase.setCampaignId(campaignId)
                }
            } else {
                adjustAttributionMap["campaign"] = campaign
                verificationUseCase.setCampaign(campaign)
            }
        }

        if (!TextUtils.isEmpty(adjustAttribution.adgroup)) {
            adjustAttributionMap["adgroup"] = adjustAttribution.adgroup
        }

        if (!TextUtils.isEmpty(adjustAttribution.creative)) {
            adjustAttributionMap["creative"] = adjustAttribution.creative
        }

        Log.d(TAG, "onAttributeChanged() called with: adjustAttributionMap = $adjustAttributionMap")
        // TODO report to thinking data

    }
}