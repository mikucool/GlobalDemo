package com.example.globaldemo.model

data class BaseHttpResult(
    val code: Int,
    val msg: String? = null,
    val data: String? = null
)