package com.todoapp.mobile.data.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresPermission
import com.todoapp.mobile.data.notification.NotificationService
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.model.AlarmItem
import com.todoapp.mobile.ui.overlay.OverlayService
import java.time.ZoneId

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(item: AlarmItem) {
        val intent = if (Settings.canDrawOverlays(context)) {
            Intent(context, OverlayService::class.java).apply {
                putExtra(OverlayService.INTENT_EXTRA_COMMAND_SHOW_OVERLAY, item.message)
                putExtra(OverlayService.INTENT_EXTRA_LONG, item.minutesBefore)
            }
        } else {
            Intent(context, NotificationService::class.java).apply {
                putExtra(NotificationService.INTENT_EXTRA_MESSAGE, item.message)
                putExtra(NotificationService.INTENT_EXTRA_LONG, item.minutesBefore)
                Log.d("Intent", item.minutesBefore.toString())
            }
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            PendingIntent.getService(
                context,
                item.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getService(
                context,
                item.hashCode(),
                Intent(context, OverlayService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
