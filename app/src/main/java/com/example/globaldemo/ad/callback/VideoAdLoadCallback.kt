package com.example.globaldemo.ad.callback

import com.example.globaldemo.model.AdFailureInformation

interface VideoAdLoadCallback {
    fun onLoaded() = run {  }
    fun onFailedToLoad(adFailureInformation: AdFailureInformation) = run {  }
}