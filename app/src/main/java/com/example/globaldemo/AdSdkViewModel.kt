package com.example.globaldemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 全局 ViewModel, 用户监听 Application 中广告SDK的初始化状态
 */
class AdSdkViewModel: ViewModel() {
    private val _adSdkInitState = MutableLiveData(AdSdkInitState())
    val adSdkInitState: LiveData<AdSdkInitState> = _adSdkInitState

    fun updateAdSdkInitState(newState: AdSdkInitState) {
        _adSdkInitState.value = newState
    }

    data class AdSdkInitState(
        val isKwaiInitialized: Boolean = false,
        val isBigoInitialized: Boolean = false,
        val isMaxInitialized: Boolean = false,
        val isAdMobInitialized: Boolean = false
    )
}