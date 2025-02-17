package com.example.globaldemo.ad

interface LoadingVideoAd {
    fun onLoadFailed() = run {}
    fun onLoadedAndDisplayed() = run {}
}