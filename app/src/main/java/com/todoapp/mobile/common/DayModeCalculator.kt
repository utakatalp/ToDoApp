package com.todoapp.mobile.common

import com.todoapp.mobile.domain.model.DayMode
import java.time.LocalTime

private const val MORNING_END_HOUR_EXCLUSIVE = 12
private const val MIDDAY_END_HOUR_EXCLUSIVE = 18

fun computeDayMode(now: LocalTime): DayMode = when (now.hour) {
    in 0..(MORNING_END_HOUR_EXCLUSIVE - 1) ->
        if (now.hour < 6) DayMode.EVENING else DayMode.MORNING
    in MORNING_END_HOUR_EXCLUSIVE..(MIDDAY_END_HOUR_EXCLUSIVE - 1) -> DayMode.MIDDAY
    else -> DayMode.EVENING
}
