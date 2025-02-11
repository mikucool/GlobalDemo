package com.example.globaldemo.verification

import android.content.Context
import com.example.globaldemo.domain.VerificationUseCase

object VerificationManager {
    fun init(context: Context, verificationUseCase: VerificationUseCase) {
        SMInitializer(verificationUseCase).initialize(context)
        // todo: init adjust
    }
}