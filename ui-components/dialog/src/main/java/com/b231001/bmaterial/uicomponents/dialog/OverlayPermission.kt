package com.b231001.bmaterial.uicomponents.dialog


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun Context.canDrawOverlays(): Boolean = Settings.canDrawOverlays(this)

fun Activity.requestOverlayPermissionIfNeeded() {
    if (!canDrawOverlays()) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        // Một số thiết bị có thể không có Activity này — nên catch/guard nếu cần.
        startActivity(intent)
    }
}
