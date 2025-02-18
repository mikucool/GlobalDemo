package com.example.globaldemo.ui.screen.test

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globaldemo.ad.AdManager
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BiddingAdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BiddingAdTestUiState())
    val uiState: StateFlow<BiddingAdTestUiState> = _uiState

    fun loadVideoAd(context: Context, adManager: AdManager) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            adManager.loadAllVideoAds(context)
            // mock delay
            delay(2000)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun displayRewardedAd(activity: Activity, adManager: AdManager) {
        adManager.tryToDisplayVideoAdWithDelay(
            activity = activity,
            onAdNotAvailable = {
                _uiState.value = _uiState.value.copy(displayMessage = "No ad available.")
            },
            videoAdShowCallback = object : VideoAdShowCallback {
                override fun onDisplayed() {
                    _uiState.value = _uiState.value.copy(displayMessage = "Ad displayed.")
                }

                override fun onFailedToDisplay() {
                    _uiState.value = _uiState.value.copy(displayMessage = "Failed to display ad.")
                }

                override fun onClicked() {
                    _uiState.value = _uiState.value.copy(displayMessage = "Ad clicked.")
                }
                override fun onClosed() {
                    _uiState.value = _uiState.value.copy(displayMessage = "Ad closed.")
                }

                override fun onRewarded() {
                    _uiState.value = _uiState.value.copy(displayMessage = "Ad rewarded.")
                }
            }
        )
    }

    data class BiddingAdTestUiState(
        val isLoading: Boolean = false,
        val displayMessage: String = ""
    )
}