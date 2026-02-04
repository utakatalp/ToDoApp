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
    private val context: Context,
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    private companion object {
        const val DAILY_PLAN_REQUEST_CODE = 10_001
    }

    private fun buildOverlayIntent(
        item: AlarmItem,
        overlayType: String = OverlayService.OVERLAY_TYPE_TASK,
    ): Intent =
        Intent(context, OverlayService::class.java).apply {
            putExtra(OverlayService.INTENT_EXTRA_COMMAND_SHOW_OVERLAY, item.message)
            putExtra(OverlayService.INTENT_EXTRA_LONG, item.minutesBefore)
            putExtra(OverlayService.INTENT_EXTRA_OVERLAY_TYPE, overlayType)
        }

    private fun buildNotificationIntent(item: AlarmItem): Intent =
        Intent(context, NotificationService::class.java).apply {
            putExtra(NotificationService.INTENT_EXTRA_MESSAGE, item.message)
            putExtra(NotificationService.INTENT_EXTRA_LONG, item.minutesBefore)
            Log.d("Intent", item.minutesBefore.toString())
        }

    private fun buildPreferredIntent(item: AlarmItem): Intent =
        if (Settings.canDrawOverlays(context)) buildOverlayIntent(item) else buildNotificationIntent(item)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(item: AlarmItem) {
        val intent = buildPreferredIntent(item)
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

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleDailyPlan(item: AlarmItem) {
        val intent =
            if (Settings.canDrawOverlays(context)) {
                buildOverlayIntent(item, OverlayService.OVERLAY_TYPE_DAILY_PLAN)
            } else {
                buildNotificationIntent(item)
            }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            PendingIntent.getService(
                context,
                DAILY_PLAN_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelDailyPlan() {
        alarmManager.cancel(
            PendingIntent.getService(
                context,
                DAILY_PLAN_REQUEST_CODE,
                Intent(context, OverlayService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        alarmManager.cancel(
            PendingIntent.getService(
                context,
                DAILY_PLAN_REQUEST_CODE,
                Intent(context, NotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(item: AlarmItem) {
        val requestCode = item.hashCode()

        alarmManager.cancel(
            PendingIntent.getService(
                context,
                requestCode,
                Intent(context, OverlayService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )

        alarmManager.cancel(
            PendingIntent.getService(
                context,
                requestCode,
                Intent(context, NotificationService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
