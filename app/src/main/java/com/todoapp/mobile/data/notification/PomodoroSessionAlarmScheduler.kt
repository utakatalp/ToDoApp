package com.todoapp.mobile.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Safety-net alarm: if the foreground service is killed under memory pressure, this fires
 * the end-of-session ringtone anyway. Scheduled when a countdown starts; cancelled on pause/skip/finish.
 */
@Singleton
class PomodoroSessionAlarmScheduler
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun scheduleAt(triggerAtMillis: Long) {
        val pi = pendingIntent()
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
            }
        }.onFailure { Timber.tag(TAG).w(it, "scheduleAt failed") }
    }

    fun cancel() {
        runCatching { alarmManager.cancel(pendingIntent()) }
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, PomodoroSessionEndReceiver::class.java).apply {
            action = PomodoroSessionEndReceiver.ACTION_SESSION_END
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val REQUEST_CODE: Int = 5151
        const val TAG: String = "PomodoroAlarm"
    }
}
