package com.example.globaldemo.ad.callback

interface VideoAdShowCallback {
    fun onDisplayed() = run {}
    fun onFailedToDisplay() = run {}
    fun onClosed() = run {}
    fun onClicked() = run {}
    fun onRewarded() = run {}
}