package com.example.globaldemo

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.databinding.ActivitySplashBinding
import com.example.globaldemo.utils.BaseActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by viewModels<SplashViewModel> { SplashViewModel.Factory }
    override fun createBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
    }

    override fun createObserver() {
        // Use repeatOnLifecycle for better lifecycle management.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect the state flow from the adSdkViewModel.
                GlobalDemoApplication.instance.adSdkViewModel.adSdkInitState.observe(this@SplashActivity) {
                    if (it.isAdMobInitialized) {
                        loadAndShowSplashAd()
                    }
                }
            }
        }
    }

    override fun initData() {
        viewModel.checkUserInfo()
    }

    private fun loadAndShowSplashAd() {
        val adManager = GlobalDemoApplication.container.adManager
        Log.d("SplashActivity", "loadAndShowSplashAd() called")
        adManager.loadAdMobSplashAd(
            context = this,
            callback = object : VideoAdLoadCallback {
                override fun onLoaded() {
                    adManager.displayAdMobSplashAd(
                        activity = this@SplashActivity,
                        callback = object : VideoAdShowCallback {}
                    )
                }
            }
        )
    }
}