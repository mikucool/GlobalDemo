package com.example.globaldemo.ui.screen.test

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.model.AdFailureInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RewardAdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RewardAdTestUiState())
    val uiState: StateFlow<RewardAdTestUiState> = _uiState

    fun updateAdPlatform(adPlatform: AdPlatform) {
        _uiState.value = _uiState.value.copy(adPlatform = adPlatform)
    }

    private fun updateRewardAdState(state: TestRewardAdState) {
        _uiState.value = _uiState.value.copy(rewardTestRewardAdState = state)
    }

    fun loadRewardAd(context: Context, biddingAdController: BiddingAdController) {
        updateRewardAdState(TestRewardAdState.LOADING)
        biddingAdController.loadAllRewardVideoAds(context, object : VideoAdLoadCallback {
            override fun onLoaded() {
                updateRewardAdState(TestRewardAdState.LOADED)
            }

            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                updateRewardAdState(TestRewardAdState.LOAD_ERROR)
            }
        })
    }

    fun displayRewardAd(activity: Activity, biddingAdController: BiddingAdController) {
        biddingAdController.displayHighestRevenueRewardVideoAd(
            activity = activity,
            videoAdShowCallback = object : VideoAdShowCallback {
                override fun onDisplayed() {
                    updateRewardAdState(TestRewardAdState.DISPLAYED)
                }

                override fun onFailedToDisplay() {
                    updateRewardAdState(TestRewardAdState.DISPLAY_ERROR)
                }

                override fun onClosed() {
                    updateRewardAdState(TestRewardAdState.CLOSED)
                }

                override fun onRewarded() {
                    updateRewardAdState(TestRewardAdState.REWARDED)
                }
            }
        )
    }


    data class RewardAdTestUiState(
        val adPlatform: AdPlatform = AdPlatform.MAX,
        val rewardTestRewardAdState: TestRewardAdState = TestRewardAdState.DEFAULT,
    )
}