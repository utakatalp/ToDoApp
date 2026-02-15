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
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.alarm.buildDailyPlanAlarmItem
import com.todoapp.mobile.domain.constants.DailyPlanDefaults
import com.todoapp.mobile.domain.repository.DailyCardPosition
import com.todoapp.mobile.domain.repository.DailyPlanPreferences
import com.todoapp.uikit.components.TDOverlayDailyPlanNotificationCard
import com.todoapp.uikit.components.TDOverlayNotificationCard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service(), LifecycleOwner, SavedStateRegistryOwner {
    @Inject
    lateinit var dailyPlanPreferences: DailyPlanPreferences

    @Inject
    lateinit var alarmScheduler: AlarmScheduler
    private lateinit var windowManager: WindowManager
    private val _lifecycleRegistry = LifecycleRegistry(this)
    private val _savedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry = _savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle = _lifecycleRegistry
    private var taskOverlayView: View? = null
    private var dailyPlanOverlayView: View? = null

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
        android.util.Log.d("OverlayService", "onStartCommand called, extras: ${intent.extras}")
        if (intent.hasExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY)) {
            val message = intent.getStringExtra(INTENT_EXTRA_COMMAND_SHOW_OVERLAY)
            val minutesBefore = intent.getLongExtra(INTENT_EXTRA_LONG, 0)
            val overlayType = intent.getStringExtra(INTENT_EXTRA_OVERLAY_TYPE) ?: OVERLAY_TYPE_TASK
            showOverlay(message.orEmpty(), minutesBefore, overlayType)
        } else if (intent.hasExtra(INTENT_EXTRA_COMMAND_HIDE_OVERLAY)) hideOverlay()
        return START_NOT_STICKY
    }

    private fun showOverlay(
        message: String,
        minutesBefore: Long,
        overlayType: String,
    ) {
        val targetViewRef =
            when (overlayType) {
                OVERLAY_TYPE_DAILY_PLAN -> dailyPlanOverlayView
                else -> taskOverlayView
            }
        if (targetViewRef != null) return

        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val layoutParams = getLayoutParams(overlayType)

        if (overlayType == OVERLAY_TYPE_DAILY_PLAN) {
            CoroutineScope(Dispatchers.IO).launch {
                val saved = dailyPlanPreferences.observeCardPosition().first()
                withContext(Dispatchers.Main) {
                    layoutParams.x = saved.cardPositionX.toInt()
                    layoutParams.y = saved.cardPositionY.toInt()
                    dailyPlanOverlayView?.let {
                        windowManager.updateViewLayout(it, layoutParams)
                    }
                }
            }
        }

        val newView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            setContent {
                var show by remember { mutableStateOf(true) }
                LaunchedEffect(show) {
                    if (!show) {
                        delay(HIDE_OVERLAY_ANIMATION_DELAY)
                        hideOverlay()
                    }
                }
                when (overlayType) {
                    OVERLAY_TYPE_DAILY_PLAN -> {
                        TDOverlayDailyPlanNotificationCard(
                            isVisible = show,
                            onDismiss = { show = false },
                            onOpenApp = {
                                show = false
                                openApp()
                            },
                            onDrag = { dx, dy ->
                                layoutParams.x += dx.toInt()
                                layoutParams.y += dy.toInt()
                                windowManager.updateViewLayout(this@apply, layoutParams)
                            },
                            onDragEnd = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    dailyPlanPreferences.saveCardPosition(
                                        DailyCardPosition(
                                            layoutParams.x.toFloat(),
                                            layoutParams.y.toFloat()
                                        )
                                    )
                                }
                            }

                        )
                    }

                    else -> {
                        TDOverlayNotificationCard(
                            message = message,
                            minutesBefore = minutesBefore,
                            show = show,
                            onDismissClick = { show = false },
                            onOpenClick = {
                                show = false
                                openApp()
                            }
                        )
                    }
                }
            }
        }
        when (overlayType) {
            OVERLAY_TYPE_DAILY_PLAN -> {
                dailyPlanOverlayView = newView
                rescheduleNextDailyPlan()
            }

            else -> taskOverlayView = newView
        }
        windowManager.addView(newView, layoutParams)
    }

    private fun rescheduleNextDailyPlan() {
        CoroutineScope(Dispatchers.IO).launch {
            val time = dailyPlanPreferences.observePlanTime().first()
                ?: DailyPlanDefaults.DEFAULT_PLAN_TIME

            val now = LocalDateTime.now()
            val item = buildDailyPlanAlarmItem(
                selectedTime = time,
                now = now,
                message = "",
            )
            alarmScheduler.schedule(item, AlarmType.DAILY_PLAN)
        }
    }

    private fun hideOverlay() {
        taskOverlayView?.let {
            windowManager.removeView(it)
            taskOverlayView = null
        }
        dailyPlanOverlayView?.let {
            windowManager.removeView(it)
            dailyPlanOverlayView = null
        }
        _lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    private fun getLayoutParams(overlayType: String): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = if (overlayType == OVERLAY_TYPE_DAILY_PLAN) {
                val bottomMarginPx = (DAILY_PLAN_BOTTOM_MARGIN_DP * resources.displayMetrics.density).toInt()
                resources.displayMetrics.heightPixels - bottomMarginPx
            } else {
                0
            }
        }
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    companion object {
        const val INTENT_EXTRA_COMMAND_SHOW_OVERLAY = "INTENT_EXTRA_COMMAND_SHOW_OVERLAY"
        const val INTENT_EXTRA_COMMAND_HIDE_OVERLAY = "INTENT_EXTRA_COMMAND_HIDE_OVERLAY"
        const val INTENT_EXTRA_LONG = "INTENT_EXTRA_LONG"
        const val HIDE_OVERLAY_ANIMATION_DELAY = 300L
        const val BOUND_MODE_NOT_SUPPORTED = "Bound mode not supported"
        const val INTENT_EXTRA_OVERLAY_TYPE = "INTENT_EXTRA_OVERLAY_TYPE"
        const val OVERLAY_TYPE_TASK = "OVERLAY_TYPE_TASK"
        const val OVERLAY_TYPE_DAILY_PLAN = "OVERLAY_TYPE_DAILY_PLAN"
        const val DAILY_PLAN_BOTTOM_MARGIN_DP = 80
    }
}
