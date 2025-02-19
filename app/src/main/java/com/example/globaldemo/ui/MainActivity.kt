package com.example.globaldemo.ui

import com.example.globaldemo.databinding.ActivityMainBinding
import com.example.globaldemo.utils.BaseActivity

class MainActivity: BaseActivity<ActivityMainBinding>() {
    override fun createBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
}