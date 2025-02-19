package com.example.globaldemo

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.globaldemo.ad.callback.VideoAdLoadCallback
import com.example.globaldemo.ad.callback.VideoAdShowCallback
import com.example.globaldemo.databinding.ActivitySplashBinding
import com.example.globaldemo.ui.MainActivity
import com.example.globaldemo.utils.BaseActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun createBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        startProgressAnimation()
    }

    override fun createObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                GlobalDemoApplication.instance.adSdkViewModel.adSdkInitState.observe(this@SplashActivity) {
                    if (it.isAdMobInitialized) loadAndShowSplashAd()
                }
            }
        }
    }

    override fun initData() {
    }

    private val progressAnimator by lazy { ValueAnimator.ofInt(0, binding.progressBar.max) }
    private fun startProgressAnimation() {
        progressAnimator.apply {
            duration = 8000
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Int
                binding.progressBar.progress = value
                if (value >= binding.progressBar.max) startMainScreen()
            }
            start()
        }
    }

    private fun loadAndShowSplashAd() {
        val adManager = GlobalDemoApplication.container.adManager
        adManager.loadAdMobSplashAd(
            callback = object : VideoAdLoadCallback {
                override fun onLoaded() {
                    adManager.displayAdMobSplashAd(
                        activity = this@SplashActivity,
                        callback = object : VideoAdShowCallback {
                            override fun onDisplayed() {
                                progressAnimator.cancel()
                            }

                            override fun onClosed() {
                                startMainScreen()
                            }
                        }
                    )
                }
            }
        )
    }

    private fun startMainScreen() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalDemoApplication.container.adManager.clearAdMobSplashCallback()
        if (progressAnimator.isRunning) {
            progressAnimator.cancel()
        }
        progressAnimator.removeAllUpdateListeners()
        progressAnimator.removeAllListeners()
    }

}