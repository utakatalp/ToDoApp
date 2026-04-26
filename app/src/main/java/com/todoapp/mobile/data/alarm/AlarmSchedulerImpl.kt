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
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.clampedDayOfMonth
import com.todoapp.mobile.ui.overlay.OverlayService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmSchedulerImpl(
    private val context: Context,
) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun schedule(
        item: AlarmItem,
        type: AlarmType,
    ) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            buildFirePendingIntent(type.getRequestCode(item), type.buildBroadcastIntent(item)),
        )
    }

    override fun cancelTask(item: AlarmItem) {
        cancelAlarm(item.hashCode())
    }

    override fun cancelScheduledAlarm(type: AlarmType) {
        val requestCode =
            when (type) {
                AlarmType.TASK -> return
                AlarmType.DAILY_PLAN -> REQUEST_CODE_DAILY_PLAN
            }
        cancelAlarm(requestCode)
    }

    private fun cancelAlarm(requestCode: Int) {
        // Empty intent matches anything previously scheduled with the same component+requestCode,
        // independent of extras (PI identity ignores extras).
        alarmManager.cancel(
            buildFirePendingIntent(requestCode, Intent(context, AlarmFireReceiver::class.java)),
        )
    }

    private fun buildFirePendingIntent(
        requestCode: Int,
        intent: Intent,
    ): PendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent.apply { setClass(context, AlarmFireReceiver::class.java) },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    private fun AlarmType.getRequestCode(item: AlarmItem): Int = when (this) {
        AlarmType.TASK -> item.hashCode()
        AlarmType.DAILY_PLAN -> REQUEST_CODE_DAILY_PLAN
    }

    private fun AlarmType.buildBroadcastIntent(item: AlarmItem): Intent = when (this) {
        AlarmType.TASK -> buildPreferredBroadcast(item, OverlayService.OVERLAY_TYPE_TASK)
        AlarmType.DAILY_PLAN -> buildPreferredBroadcast(item, OverlayService.OVERLAY_TYPE_DAILY_PLAN)
    }

    private fun buildPreferredBroadcast(
        item: AlarmItem,
        overlayType: String,
    ): Intent = if (Settings.canDrawOverlays(context)) {
        buildOverlayBroadcast(item, overlayType)
    } else {
        buildNotificationBroadcast(item)
    }

    private fun buildOverlayBroadcast(
        item: AlarmItem,
        overlayType: String,
    ): Intent = Intent(context, AlarmFireReceiver::class.java).apply {
        action = AlarmFireReceiver.ACTION_FIRE
        putExtra(AlarmFireReceiver.EXTRA_FIRE_TARGET, AlarmFireReceiver.FIRE_TARGET_OVERLAY)
        putExtra(OverlayService.INTENT_EXTRA_COMMAND_SHOW_OVERLAY, item.message)
        putExtra(OverlayService.INTENT_EXTRA_LONG, item.minutesBefore)
        putExtra(OverlayService.INTENT_EXTRA_OVERLAY_TYPE, overlayType)
    }

    private fun buildNotificationBroadcast(item: AlarmItem): Intent = Intent(
        context,
        AlarmFireReceiver::class.java,
    ).apply {
        action = AlarmFireReceiver.ACTION_FIRE
        putExtra(AlarmFireReceiver.EXTRA_FIRE_TARGET, AlarmFireReceiver.FIRE_TARGET_NOTIFICATION)
        putExtra(NotificationService.INTENT_EXTRA_MESSAGE, item.message)
        putExtra(NotificationService.INTENT_EXTRA_LONG, item.minutesBefore)
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    override fun scheduleRecurring(
        taskId: Long,
        recurrence: Recurrence,
        anchorDate: LocalDate,
        hour: Int,
        minute: Int,
        message: String,
    ) {
        if (recurrence == Recurrence.NONE) return
        val nextFire = computeNextFire(recurrence, anchorDate, hour, minute, LocalDateTime.now())
        val intent = buildRecurringTaskBroadcast(
            taskId = taskId,
            recurrence = recurrence,
            anchorDate = anchorDate,
            hour = hour,
            minute = minute,
            message = message,
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextFire.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            buildFirePendingIntent(recurringRequestCode(taskId), intent),
        )
    }

    override fun cancelRecurring(taskId: Long) {
        cancelAlarm(recurringRequestCode(taskId))
    }

    private fun computeNextFire(
        recurrence: Recurrence,
        anchorDate: LocalDate,
        hour: Int,
        minute: Int,
        now: LocalDateTime,
    ): LocalDateTime {
        val time = LocalTime.of(hour, minute)
        return when (recurrence) {
            Recurrence.NONE -> error("scheduleRecurring called with NONE")
            Recurrence.DAILY -> {
                val today = LocalDate.now().atTime(time)
                if (today.isAfter(now)) today else today.plusDays(1)
            }
            Recurrence.WEEKLY -> {
                var candidate = LocalDate.now().atTime(time)
                while (candidate.dayOfWeek != anchorDate.dayOfWeek || !candidate.isAfter(now)) {
                    candidate = candidate.plusDays(1)
                }
                candidate
            }
            Recurrence.MONTHLY -> nextMonthlyFire(anchorDate.dayOfMonth, time, now)
            Recurrence.YEARLY -> nextYearlyFire(anchorDate, time, now)
        }
    }

    private fun nextMonthlyFire(anchorDay: Int, time: LocalTime, now: LocalDateTime): LocalDateTime {
        var year = now.year
        var month = now.monthValue
        repeat(MAX_MONTH_LOOKAHEAD) {
            val day = clampedDayOfMonth(anchorDay, year, month)
            val candidate = LocalDate.of(year, month, day).atTime(time)
            if (candidate.isAfter(now)) return candidate
            month++
            if (month > 12) {
                month = 1
                year++
            }
        }
        error("nextMonthlyFire: could not find a future fire within $MAX_MONTH_LOOKAHEAD months")
    }

    private fun nextYearlyFire(anchor: LocalDate, time: LocalTime, now: LocalDateTime): LocalDateTime {
        var year = now.year
        repeat(MAX_YEAR_LOOKAHEAD) {
            val day = clampedDayOfMonth(anchor.dayOfMonth, year, anchor.monthValue)
            val candidate = LocalDate.of(year, anchor.monthValue, day).atTime(time)
            if (candidate.isAfter(now)) return candidate
            year++
        }
        error("nextYearlyFire: could not find a future fire within $MAX_YEAR_LOOKAHEAD years")
    }

    private fun buildRecurringTaskBroadcast(
        taskId: Long,
        recurrence: Recurrence,
        anchorDate: LocalDate,
        hour: Int,
        minute: Int,
        message: String,
    ): Intent {
        val base = if (Settings.canDrawOverlays(context)) {
            Intent(context, AlarmFireReceiver::class.java).apply {
                action = AlarmFireReceiver.ACTION_FIRE
                putExtra(AlarmFireReceiver.EXTRA_FIRE_TARGET, AlarmFireReceiver.FIRE_TARGET_OVERLAY)
                putExtra(OverlayService.INTENT_EXTRA_COMMAND_SHOW_OVERLAY, message)
                putExtra(OverlayService.INTENT_EXTRA_LONG, 0L)
                putExtra(OverlayService.INTENT_EXTRA_OVERLAY_TYPE, OverlayService.OVERLAY_TYPE_TASK)
            }
        } else {
            Intent(context, AlarmFireReceiver::class.java).apply {
                action = AlarmFireReceiver.ACTION_FIRE
                putExtra(AlarmFireReceiver.EXTRA_FIRE_TARGET, AlarmFireReceiver.FIRE_TARGET_NOTIFICATION)
                putExtra(NotificationService.INTENT_EXTRA_MESSAGE, message)
                putExtra(NotificationService.INTENT_EXTRA_LONG, 0L)
            }
        }
        return base.apply {
            putExtra(AlarmFireReceiver.EXTRA_RECURRENCE, recurrence.name)
            putExtra(AlarmFireReceiver.EXTRA_ANCHOR_EPOCH_DAY, anchorDate.toEpochDay())
            putExtra(AlarmFireReceiver.EXTRA_DAILY_TASK_ID, taskId)
            putExtra(AlarmFireReceiver.EXTRA_DAILY_HOUR, hour)
            putExtra(AlarmFireReceiver.EXTRA_DAILY_MINUTE, minute)
            putExtra(AlarmFireReceiver.EXTRA_DAILY_MESSAGE, message)
        }
    }

    private fun recurringRequestCode(taskId: Long): Int = (RECURRING_TASK_REQUEST_BASE + taskId).toInt()

    private companion object {
        const val REQUEST_CODE_DAILY_PLAN = 10_001

        // Distinct namespace from item.hashCode()-based TASK request codes.
        const val RECURRING_TASK_REQUEST_BASE = 0x0100_0000L

        const val MAX_MONTH_LOOKAHEAD = 13
        const val MAX_YEAR_LOOKAHEAD = 5
    }
}
