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

class InterstitialAdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InterstitialAdTestUiState())
    val uiState: StateFlow<InterstitialAdTestUiState> = _uiState

    fun updateAdPlatform(adPlatform: AdPlatform) {
        _uiState.value = _uiState.value.copy(adPlatform = adPlatform)
    }

    private fun updateInterstitialAdState(state: TestInterstitialAdState) {
        _uiState.value = _uiState.value.copy(interstitialAdState = state)
    }

    fun loadInterstitialAd(context: Context, biddingAdController: BiddingAdController) {
        updateInterstitialAdState(TestInterstitialAdState.LOADING)
        biddingAdController.loadAllRewardVideoAds(context, object : VideoAdLoadCallback {
            override fun onLoaded() {
                updateInterstitialAdState(TestInterstitialAdState.LOADED)
            }

            override fun onFailedToLoad(adFailureInformation: AdFailureInformation) {
                updateInterstitialAdState(TestInterstitialAdState.LOAD_ERROR)
            }
        })
    }

    fun displayInterstitialAd(activity: Activity, biddingAdController: BiddingAdController) {
        biddingAdController.displayHighestRevenueInterstitialAd(
            activity = activity,
            videoAdShowCallback = object : VideoAdShowCallback {
                override fun onDisplayed() {
                    updateInterstitialAdState(TestInterstitialAdState.DISPLAYED)
                }

                override fun onFailedToDisplay() {
                    super.onFailedToDisplay()
                    updateInterstitialAdState(TestInterstitialAdState.DISPLAY_ERROR)
                }

                override fun onClosed() {
                    super.onClosed()
                    updateInterstitialAdState(TestInterstitialAdState.CLOSED)
                }
            }
        )
    }


    data class InterstitialAdTestUiState(
        val adPlatform: AdPlatform = AdPlatform.MAX,
        val interstitialAdState: TestInterstitialAdState = TestInterstitialAdState.DEFAULT,
    )
}