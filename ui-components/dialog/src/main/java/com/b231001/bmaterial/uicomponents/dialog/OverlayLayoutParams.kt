package com.b231001.bmaterial.uicomponents.dialog

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager

fun Context.defaultOverlayParams(): WindowManager.LayoutParams {
    return WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // API 26+
        (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS),
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.START
        x = 24; y = 120
    }
}

