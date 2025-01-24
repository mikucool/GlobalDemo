package com.example.globaldemo.ui.screen.test

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.BiddingAdController
import com.example.globaldemo.ad.AdPlatform
import com.example.globaldemo.ad.RewardAdCallback
import com.example.globaldemo.ad.RewardAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdTestUiState())
    val uiState: StateFlow<AdTestUiState> = _uiState

    fun updateAdPlatform(adPlatform: AdPlatform) {
        _uiState.value = _uiState.value.copy(adPlatform = adPlatform)
    }

    private fun updateRewardAdState(rewardAdState: RewardAdState) {
        _uiState.value = _uiState.value.copy(rewardAdState = rewardAdState)
    }

    fun loadAd(context: Context, biddingAdController: BiddingAdController) {
        updateRewardAdState(RewardAdState.LOADING)
        biddingAdController.loadRewardVideoAds(context, object : RewardAdCallback {
            override fun onLoaded() {
                super.onLoaded()
                updateRewardAdState(RewardAdState.LOADED)
            }

            override fun onFailedToLoad() {
                super.onFailedToLoad()
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

    data class AdTestUiState(
        val adPlatform: AdPlatform = AdPlatform.MAX,
        val rewardAdState: RewardAdState = RewardAdState.DEFAULT
    )
}