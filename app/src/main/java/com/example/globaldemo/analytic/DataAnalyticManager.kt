package com.example.globaldemo.analytic

import android.content.Context
import com.example.globaldemo.domain.VerificationUseCase

object DataAnalyticManager {
    fun init(context: Context, verificationUseCase: VerificationUseCase) {
        ThinkingDataInitializer(verificationUseCase).initialize(context)
        // TODO: init firebase
    }
}