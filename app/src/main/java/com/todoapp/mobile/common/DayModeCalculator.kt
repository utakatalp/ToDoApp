package com.todoapp.mobile.common

import com.todoapp.mobile.domain.model.DayMode
import java.time.LocalTime

private const val MORNING_START_HOUR_INCLUSIVE = 6
private const val MORNING_END_HOUR_EXCLUSIVE = 12
private const val MIDDAY_END_HOUR_EXCLUSIVE = 18
private const val EVENING_END_HOUR_EXCLUSIVE = 22

fun computeDayMode(now: LocalTime): DayMode = when (now.hour) {
    in MORNING_START_HOUR_INCLUSIVE..(MORNING_END_HOUR_EXCLUSIVE - 1) -> DayMode.MORNING
    in MORNING_END_HOUR_EXCLUSIVE..(MIDDAY_END_HOUR_EXCLUSIVE - 1) -> DayMode.MIDDAY
    in MIDDAY_END_HOUR_EXCLUSIVE..(EVENING_END_HOUR_EXCLUSIVE - 1) -> DayMode.EVENING
    else -> DayMode.NIGHT
}
