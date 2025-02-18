package com.example.globaldemo

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.example.globaldemo.databinding.ActivitySplashBinding
import com.example.globaldemo.utils.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by viewModels<SplashViewModel> { SplashViewModel.Factory }
    override fun createBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
    }

    override fun createObserver() {
        super.createObserver()
    }
}