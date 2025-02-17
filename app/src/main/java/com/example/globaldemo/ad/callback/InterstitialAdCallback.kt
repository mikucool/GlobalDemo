package com.example.globaldemo.ad.callback

import com.example.globaldemo.model.AdFailureInformation

interface InterstitialAdCallback {
    fun onLoaded() = run { }
    fun onFailedToLoad(adFailureInformation: AdFailureInformation) = run { }
    fun onDisplayed() = run { }
    fun onFailedToDisplay() = run { }
    fun onClosed() = run { }
    fun onClicked() = run { }
}