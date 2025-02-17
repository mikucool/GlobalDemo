package com.example.globaldemo.ui.screen.test

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.AdManager
import com.example.globaldemo.ad.constant.RewardAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BiddingAdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BiddingAdTestUiState())
    val uiState: StateFlow<BiddingAdTestUiState> = _uiState

    private fun updateRewardAdState(rewardAdState: RewardAdState) {
        _uiState.value = _uiState.value.copy(rewardAdState = rewardAdState)
    }

    fun loadAd(context: Context, adManager: AdManager) {
        updateRewardAdState(RewardAdState.LOADING)
        adManager.loadAllVideoAds(context)
    }

    fun displayRewardedAd(activity: Activity, adManager: AdManager) {
        adManager.displayRewardedAd(activity)
    }


    data class BiddingAdTestUiState(
        val rewardAdState: RewardAdState = RewardAdState.DEFAULT
    )
}