package com.example.globaldemo.ad

import com.example.globaldemo.ad.constant.AdPlatform
import com.example.globaldemo.ad.controller.AdMobController
import com.example.globaldemo.ad.controller.BiddingAdController
import com.example.globaldemo.ad.controller.BigoBiddingAdController
import com.example.globaldemo.ad.controller.KwaiBiddingAdController
import com.example.globaldemo.ad.controller.MaxBiddingAdController
import com.example.globaldemo.model.AdConfiguration

object AdControllerFactory {
    fun generateAdControllers(adConfigurations: List<AdConfiguration>): List<BiddingAdController> {
        val controllers = mutableListOf<BiddingAdController>()
        for (adConfiguration in adConfigurations) {
            when (adConfiguration.adPlatform) {
                AdPlatform.BIGO -> controllers.add(BigoBiddingAdController(adConfiguration))
                AdPlatform.MAX -> controllers.add(MaxBiddingAdController(adConfiguration))
                AdPlatform.KWAI -> controllers.add(KwaiBiddingAdController(adConfiguration))
                AdPlatform.ADMOB -> controllers.add(AdMobController(adConfiguration))
                else -> {}
            }
        }
        return controllers
    }
}