package com.b231001.bmaterial.uicomponents.dialog

import com.b231001.bmaterial.uicore.tokens.BTheme
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelStore
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private var composeView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val viewModelStore = ViewModelStore()
        val viewModelOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = viewModelStore
        }

        val lifecycleOwner = ServiceLifecycleOwner().apply { onCreate() }

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(viewModelOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)

            setContent { BTheme { OverlayRoot(onClose = { stopSelf() }) } }
        }

        wm.addView(composeView, applicationContext.defaultOverlayParams())
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let { runCatching { wm.removeView(it) } }
        composeView = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = "overlay_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(channelId, "Overlay", NotificationManager.IMPORTANCE_MIN)
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Overlay running")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .build()
    }
}

