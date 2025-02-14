package com.example.globaldemo.ui.screen.test

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.callback.InterstitialAdCallback
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.callback.RewardAdCallback
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
        biddingAdController.loadAllRewardVideoAds(context, object : RewardAdCallback {
            override fun onLoaded() {
                super.onLoaded()
                updateRewardAdState(RewardAdState.LOADED)
            }

            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                super.onFailedToLoad(adFailureInformation)
                updateRewardAdState(RewardAdState.LOAD_ERROR)
            }

            override fun onDisplayed() {
                super.onDisplayed()
                updateRewardAdState(RewardAdState.DISPLAYED)
            }

            override fun onFailedToDisplay() {
                super.onFailedToDisplay()
                updateRewardAdState(RewardAdState.DISPLAY_ERROR)
            }

            override fun onRewarded() {
                super.onRewarded()
                updateRewardAdState(RewardAdState.REWARDED)
            }

            override fun onClosed() {
                super.onClosed()
                updateRewardAdState(RewardAdState.CLOSED)
            }
        })

    }

    fun loadInterstitialAd(context: Context, biddingAdController: BiddingAdController) {
        updateInterstitialAdState(InterstitialAdState.LOADING)
        biddingAdController.loadInterstitialAds(context, object : InterstitialAdCallback {
            override fun onLoaded() {
                super.onLoaded()
                updateInterstitialAdState(InterstitialAdState.LOADED)
            }
            override fun onFailedToLoad() {
                super.onFailedToLoad()
                updateInterstitialAdState(InterstitialAdState.LOAD_ERROR)
            }
            override fun onDisplayed() {
                super.onDisplayed()
                updateInterstitialAdState(InterstitialAdState.DISPLAYED)
            }

            override fun onFailedToDisplay() {
                super.onFailedToDisplay()
                updateInterstitialAdState(InterstitialAdState.DISPLAY_ERROR)
            }

            override fun onClosed() {
                super.onClosed()
                updateInterstitialAdState(InterstitialAdState.CLOSED)
            }
        })
    }

    data class AdTestUiState(
        val adPlatform: AdPlatform = AdPlatform.MAX,
        val rewardAdState: RewardAdState = RewardAdState.DEFAULT,
        val interstitialAdState: InterstitialAdState = InterstitialAdState.DEFAULT
    )
}