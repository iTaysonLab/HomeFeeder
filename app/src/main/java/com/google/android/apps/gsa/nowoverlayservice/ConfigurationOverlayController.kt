package com.google.android.apps.gsa.nowoverlayservice

import android.app.Service
import android.content.Context
import android.content.res.Configuration

import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.libraries.gsa.d.a.OverlaysController
import com.google.android.libraries.gsa.d.a.v

import ua.itaysonlab.homefeeder.overlay.OverlayKt

class ConfigurationOverlayController(private val service: Service) : OverlaysController(service) {
    override fun Hx(): Int {
        return 24
    }

    override fun createController(
        configuration: Configuration?,
        i: Int,
        i2: Int
    ): OverlayController {
        var context: Context = service
        if (configuration != null) {
            context = context.createConfigurationContext(configuration)
        }
        return OverlayKt(context)
    }

    override fun HA(): v {
        return v()
    }
}
