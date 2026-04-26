package com.todoapp.mobile.domain.alarm

import com.todoapp.mobile.domain.model.AlarmItem
import com.todoapp.mobile.domain.model.Recurrence
import java.time.LocalDate

enum class AlarmType {
    TASK,
    DAILY_PLAN,
}

interface AlarmScheduler {
    fun schedule(
        item: AlarmItem,
        type: AlarmType,
    )

    fun cancelTask(item: AlarmItem)

    fun cancelScheduledAlarm(type: AlarmType)

    /**
     * Schedules the next firing for a recurring task. Computes the next instant per the
     * recurrence rule, anchored at [anchorDate] (the date the user picked when creating).
     * Caller must pass `recurrence != NONE`.
     */
    fun scheduleRecurring(
        taskId: Long,
        recurrence: Recurrence,
        anchorDate: LocalDate,
        hour: Int,
        minute: Int,
        message: String,
    )

    fun cancelRecurring(taskId: Long)
}
