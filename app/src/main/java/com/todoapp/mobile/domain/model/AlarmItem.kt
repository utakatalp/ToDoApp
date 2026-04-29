package com.todoapp.mobile.domain.model

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val message: String,
    val minutesBefore: Long,
    /**
     * Stable request-code seed for one-shot task alarms. When set, AlarmScheduler
     * uses [taskId] (not the AlarmItem hash) so re-scheduling the same task —
     * even with a changed time/message — replaces the existing alarm rather
     * than creating a second one.
     */
    val taskId: Long? = null,
)
