package com.todoapp.mobile.data.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.model.AlarmItem
import com.todoapp.mobile.ui.overlay.OverlayService
import com.todoapp.mobile.ui.overlay.OverlayServiceConstants
import java.time.ZoneId

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, OverlayService::class.java).apply {
            putExtra(OverlayServiceConstants.INTENT_EXTRA_COMMAND_SHOW_OVERLAY, item.message)
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
