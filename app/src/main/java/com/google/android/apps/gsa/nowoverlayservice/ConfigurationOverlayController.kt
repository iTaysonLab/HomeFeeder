package com.google.android.apps.gsa.nowoverlayservice

import android.app.Service
import android.content.Context
import android.content.res.Configuration

import com.google.android.libraries.gsa.d.a.OverlayController
import com.google.android.libraries.gsa.d.a.OverlaysController
import com.google.android.libraries.gsa.d.a.v

import ua.itaysonlab.homefeeder.overlay.OverlayKt

class ConfigurationOverlayController(private val service: Service) : OverlaysController(service) {
    override fun Hx() = 24

    override fun createController(
        configuration: Configuration?,
        i: Int,
        i2: Int
    ): OverlayController = OverlayKt(if (configuration != null) service.createConfigurationContext(configuration) else service)

    override fun HA() = v()
}
