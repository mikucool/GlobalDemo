package com.example.globaldemo.model

data class BaseHttpResult<T>(
    val code: Int,
    val msg: String? = null,
    val data: T? = null
)