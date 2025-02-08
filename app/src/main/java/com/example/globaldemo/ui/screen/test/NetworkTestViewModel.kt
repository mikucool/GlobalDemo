package com.example.globaldemo.ui.screen.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globaldemo.GlobalDemoApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NetworkTestViewModel : ViewModel() {
    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    fun testGet() {
        val appDataSourceUseCase = GlobalDemoApplication.container.appDataSourceUseCase
        viewModelScope.launch { _response.value = appDataSourceUseCase.testGet().toString() }
    }

    fun testPost() {
        val appDataSourceUseCase = GlobalDemoApplication.container.appDataSourceUseCase
        viewModelScope.launch { _response.value = appDataSourceUseCase.testPost("this is a test post request body").toString() }

    }
}