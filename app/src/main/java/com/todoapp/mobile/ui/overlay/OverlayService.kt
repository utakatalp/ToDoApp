package com.todoapp.mobile.ui.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.todoapp.mobile.MainActivity
import com.todoapp.mobile.ui.overlay.OverlayServiceConstants.BOUND_MODE_NOT_SUPPORTED
import com.todoapp.mobile.ui.overlay.OverlayServiceConstants.INTENT_EXTRA_COMMAND_HIDE_OVERLAY
import com.todoapp.mobile.ui.overlay.OverlayServiceConstants.INTENT_EXTRA_COMMAND_SHOW_OVERLAY
import com.todoapp.uikit.components.TDOverlayNotificationCard
import kotlinx.coroutines.delay

object OverlayServiceConstants {
    const val INTENT_EXTRA_COMMAND_SHOW_OVERLAY = "INTENT_EXTRA_COMMAND_SHOW_OVERLAY"
    const val INTENT_EXTRA_COMMAND_HIDE_OVERLAY = "INTENT_EXTRA_COMMAND_HIDE_OVERLAY"
    const val HIDE_OVERLAY_ANIMATION_DELAY = 300L
    const val BOUND_MODE_NOT_SUPPORTED = "Bound mode not supported"
    const val WINDOW_MANAGER_LAYOUT_HEIGHT = 400
}

class OverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    lateinit var windowManager: WindowManager
    private val _lifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = _savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle = _lifecycleRegistry
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException(BOUND_MODE_NOT_SUPPORTED)
    }
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        _savedStateRegistryController.performAttach()
        _savedStateRegistryController.performRestore(null)
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.hasExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY)) {
            val message = intent.getStringExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY)
            showOverlay(message.orEmpty())
        } else if (intent.hasExtra(INTENT_EXTRA_COMMAND_HIDE_OVERLAY)) hideOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay(message: String) {
        if (overlayView != null) return

        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        overlayView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                var show by remember { mutableStateOf(true) }
                LaunchedEffect(show) {
                    if (!show) {
                        delay(OverlayServiceConstants.HIDE_OVERLAY_ANIMATION_DELAY)
                        hideOverlay()
                    }
                }
                TDOverlayNotificationCard(
                    message = message,
                    show = show,
                    onDismissClick = { show = false },
                    onOpenClick = {
                        show = false
                        openApp()
                    }
                )
            }
        }
        windowManager.addView(overlayView, getLayoutParams())
    }

    private fun hideOverlay() {
        if (overlayView == null) return
        windowManager.removeView(overlayView)
        overlayView = null
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    private fun getLayoutParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            OverlayServiceConstants.WINDOW_MANAGER_LAYOUT_HEIGHT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP
        }
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}
