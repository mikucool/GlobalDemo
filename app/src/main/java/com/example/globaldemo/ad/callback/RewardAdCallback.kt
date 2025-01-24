package com.example.globaldemo.ad.callback

interface RewardAdCallback {
    fun onLoaded() = run { }
    fun onFailedToLoad() = run { }
    fun onDisplayed() = run { }
    fun onFailedToDisplay() = run { }
    fun onRewarded() = run { }
    fun onClosed() = run { }
    fun onClicked() = run { }
    fun onOpened() = run { }
}