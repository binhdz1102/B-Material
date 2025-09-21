package com.b231001.bmaterial.uicomponents.dialog


import android.content.Context
import android.content.Intent

object OverlayController {
    fun start(context: Context) {
        if (!context.canDrawOverlays()) return
        context.startService(Intent(context, OverlayService::class.java))
    }

    fun stop(context: Context) {
        context.stopService(Intent(context, OverlayService::class.java))
    }
}

