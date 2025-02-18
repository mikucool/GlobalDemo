package com.example.globaldemo.ui.screen.test

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.constant.InterstitialAdState
import com.example.globaldemo.ad.constant.RewardAdState
import com.example.globaldemo.model.AdFailureInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdPlatformTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdTestUiState())
    val uiState: StateFlow<AdTestUiState> = _uiState

    fun updateAdPlatform(adPlatform: AdPlatform) {
        _uiState.value = _uiState.value.copy(adPlatform = adPlatform)
    }

    private fun updateRewardAdState(rewardAdState: RewardAdState) {
        _uiState.value = _uiState.value.copy(rewardAdState = rewardAdState)
    }

    private fun updateInterstitialAdState(interstitialAdState: InterstitialAdState) {
        _uiState.value = _uiState.value.copy(interstitialAdState = interstitialAdState)
    }

    fun loadRewardAd(context: Context, biddingAdController: BiddingAdController) {
        updateRewardAdState(RewardAdState.LOADING)
        biddingAdController.loadAllRewardVideoAds(context, object : VideoAdLoadCallback {
            override fun onLoaded() {
                updateRewardAdState(RewardAdState.LOADED)
            }
            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                updateRewardAdState(RewardAdState.LOAD_ERROR)
            }
        })
    }

    fun loadInterstitialAd(context: Context, biddingAdController: BiddingAdController) {
        biddingAdController.loadAllInterstitialAds(context)
    }

    data class AdTestUiState(
        val adPlatform: AdPlatform = AdPlatform.MAX,
        val rewardAdState: RewardAdState = RewardAdState.DEFAULT,
        val interstitialAdState: InterstitialAdState = InterstitialAdState.DEFAULT
    )
}