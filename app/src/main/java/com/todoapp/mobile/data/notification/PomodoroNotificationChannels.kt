package com.todoapp.mobile.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.todoapp.mobile.R

object PomodoroNotificationChannels {
    // v2 bumps importance from LOW to DEFAULT so Samsung's Now Bar / lockscreen surface
    // recognises the session as a live activity. Channels are immutable after creation,
    // so the ID change is required for users who already received the v1 channel.
    const val LIVE_CHANNEL_ID: String = "pomodoro_live_channel_v2"
    private const val LEGACY_CHANNEL_ID_V1: String = "pomodoro_live_channel"

    fun ensurePomodoroChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        runCatching { manager.deleteNotificationChannel(LEGACY_CHANNEL_ID_V1) }
        if (manager.getNotificationChannel(LIVE_CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            LIVE_CHANNEL_ID,
            context.getString(R.string.pomodoro_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.pomodoro_notification_channel_description)
            enableVibration(false)
            setShowBadge(false)
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        manager.createNotificationChannel(channel)
    }
}
