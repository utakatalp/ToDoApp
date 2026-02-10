package com.todoapp.mobile.data.alarm

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.annotation.RequiresPermission
import com.todoapp.mobile.data.notification.NotificationService
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.model.AlarmItem
import com.todoapp.mobile.ui.overlay.OverlayService
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmSchedulerImpl(
    private val context: Context,
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(item: AlarmItem, type: AlarmType) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            PendingIntent.getService(
                context,
                type.getRequestCode(item),
                type.buildIntent(item),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelTask(item: AlarmItem) {
        cancelAlarm(item.hashCode())
    }

    override fun cancelScheduledAlarm(type: AlarmType) {
        val requestCode = when (type) {
            AlarmType.TASK -> return
            AlarmType.DAILY_PLAN -> REQUEST_CODE_DAILY_PLAN
        }
        cancelAlarm(requestCode)
    }

    private fun cancelAlarm(requestCode: Int) {
        cancelPendingIntent(requestCode, OverlayService::class.java)
        cancelPendingIntent(requestCode, NotificationService::class.java)
    }

    private fun cancelPendingIntent(requestCode: Int, serviceClass: Class<*>) {
        alarmManager.cancel(
            PendingIntent.getService(
                context,
                requestCode,
                Intent(context, serviceClass),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun AlarmType.getRequestCode(item: AlarmItem): Int = when (this) {
        AlarmType.TASK -> item.hashCode()
        AlarmType.DAILY_PLAN -> REQUEST_CODE_DAILY_PLAN
    }

    private fun AlarmType.buildIntent(item: AlarmItem): Intent = when (this) {
        AlarmType.TASK -> buildPreferredIntent(item, OverlayService.OVERLAY_TYPE_TASK)
        AlarmType.DAILY_PLAN -> buildPreferredIntent(item, OverlayService.OVERLAY_TYPE_DAILY_PLAN)
    }

    private fun buildPreferredIntent(item: AlarmItem, overlayType: String): Intent =
        if (Settings.canDrawOverlays(context)) buildOverlayIntent(item, overlayType) else buildNotificationIntent(item)

    private fun buildOverlayIntent(
        item: AlarmItem,
        overlayType: String,
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

    private companion object {
        const val REQUEST_CODE_DAILY_PLAN = 10_001
    }
}
