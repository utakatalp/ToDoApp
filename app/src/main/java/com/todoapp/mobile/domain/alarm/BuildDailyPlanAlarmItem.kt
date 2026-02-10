package com.todoapp.mobile.domain.alarm

import com.todoapp.mobile.domain.model.AlarmItem
import java.time.LocalDateTime
import java.time.LocalTime

fun buildDailyPlanAlarmItem(
    selectedTime: LocalTime,
    now: LocalDateTime,
    message: String,
): AlarmItem {
    val todayCandidate = LocalDateTime.of(now.toLocalDate(), selectedTime)
    val nextTrigger = if (todayCandidate.isAfter(now)) todayCandidate else todayCandidate.plusDays(1)
    return AlarmItem(time = nextTrigger, message = message, minutesBefore = 0L)
}
