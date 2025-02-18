package com.example.globaldemo.utils

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>: AppCompatActivity() {
    protected val binding by lazy { createBinding() }
    protected abstract fun createBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemUi()
        setContentView(binding.root)
        initView()
        createObserver()
        initData()
    }

    protected open fun initView() {

    }

    protected open fun initData() {

    }

    protected open fun createObserver() {

    }

}