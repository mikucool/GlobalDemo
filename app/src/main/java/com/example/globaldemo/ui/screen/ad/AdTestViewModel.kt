package com.example.globaldemo.ui.screen.ad

import androidx.lifecycle.ViewModel
import com.example.globaldemo.ad.AdPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdTestViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdTestUiState(AdPlatform.MAX))
    val uiState: StateFlow<AdTestUiState> = _uiState

    fun updateAdPlatform(adPlatform: AdPlatform) {
        _uiState.value = AdTestUiState(adPlatform)
    }
    data class AdTestUiState(val adPlatform: AdPlatform)
}