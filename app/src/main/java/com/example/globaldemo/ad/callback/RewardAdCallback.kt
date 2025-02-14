package com.example.globaldemo.ad.callback

import com.example.globaldemo.model.AdFailureInformation

interface RewardAdCallback {
    fun onLoaded() = run { }
    fun onFailedToLoad(adFailureInformation: AdFailureInformation) = run { }
    fun onDisplayed() = run { }
    fun onFailedToDisplay() = run { }
    fun onRewarded() = run { }
    fun onClosed() = run { }
    fun onClicked() = run { }
    fun onOpened() = run { }
}