package com.example.globaldemo.ui.screen.test

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.AdUseCase
import com.example.globaldemo.ad.constant.RewardAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BiddingAdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BiddingAdTestUiState())
    val uiState: StateFlow<BiddingAdTestUiState> = _uiState

    private fun updateRewardAdState(rewardAdState: RewardAdState) {
        _uiState.value = _uiState.value.copy(rewardAdState = rewardAdState)
    }

    fun loadAd(context: Context, adUseCase: AdUseCase) {
        updateRewardAdState(RewardAdState.LOADING)
        adUseCase.preloadAllRewardAds(context)
    }

    fun displayRewardedAd(activity: Activity, adUseCase: AdUseCase) {
        adUseCase.displayRewardedAd(
            activity,
            onTimeout = { updateRewardAdState(RewardAdState.LOAD_ERROR) }
        )
    }


    data class BiddingAdTestUiState(
        val rewardAdState: RewardAdState = RewardAdState.DEFAULT
    )
}