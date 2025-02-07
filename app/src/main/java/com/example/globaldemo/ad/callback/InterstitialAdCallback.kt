package com.example.globaldemo.ad.callback

interface InterstitialAdCallback {
    fun onLoaded() = run { }
    fun onFailedToLoad() = run { }
    fun onDisplayed() = run { }
    fun onFailedToDisplay() = run { }
    fun onClosed() = run { }
    fun onClicked() = run { }
}