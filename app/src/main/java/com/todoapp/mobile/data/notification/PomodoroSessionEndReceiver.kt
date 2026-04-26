package com.todoapp.mobile.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.todoapp.mobile.R
import com.todoapp.mobile.common.RingtoneHolder
import com.example.uikit.R as UikitR

class PomodoroSessionEndReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PomodoroNotificationChannels.ensurePomodoroChannel(context)
        RingtoneHolder().play(context = context.applicationContext)

        val notification = NotificationCompat
            .Builder(context, PomodoroNotificationChannels.LIVE_CHANNEL_ID)
            .setSmallIcon(UikitR.drawable.ic_sand_clock)
            .setContentTitle(context.getString(R.string.pomodoro_notification_session_complete))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        runCatching {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_END, notification)
        }
    }

    companion object {
        const val ACTION_SESSION_END: String = "com.todoapp.mobile.pomodoro.action.SESSION_END_BACKUP"
        private const val NOTIFICATION_ID_END: Int = 4243
    }
}
