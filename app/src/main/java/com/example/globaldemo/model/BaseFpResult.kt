package com.example.globaldemo.model

import com.google.gson.annotations.SerializedName

data class BaseFpResult(
    @SerializedName("configBeans")
    val fpConfigurations: List<FpConfiguration>,
    val fpExtendJson: FpExtendJson
)

data class FpConfiguration(
    val id: String,
    val jsonContent: String
)

data class FpExtendJson(
    @SerializedName("firebase_ad_ratio")
    val firebaseAdRatio: Double?,
    var userCountry: String?,
    var userId: String?
)
