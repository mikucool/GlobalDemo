package com.example.globaldemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.globaldemo.GlobalDemoApplication.Companion.container
import com.example.globaldemo.domain.AppDataSourceUseCase
import kotlinx.coroutines.launch

class SplashViewModel(private val appDataSourceUseCase: AppDataSourceUseCase): ViewModel() {

    init {
        viewModelScope.launch {
            // TODO: get data
        }
    }

    fun checkUserInfo() {
        // TODO: check user info
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val appDataSourceUseCase = container.appDataSourceUseCase
                SplashViewModel(appDataSourceUseCase = appDataSourceUseCase)
            }
        }
    }
}