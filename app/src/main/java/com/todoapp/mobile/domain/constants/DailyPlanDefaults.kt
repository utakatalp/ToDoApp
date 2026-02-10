package com.todoapp.mobile.domain.constants

import java.time.LocalTime

object DailyPlanDefaults {
    private const val DEFAULT_HOUR = 9
    private const val DEFAULT_MINUTE = 0

    val DEFAULT_PLAN_TIME: LocalTime = LocalTime.of(DEFAULT_HOUR, DEFAULT_MINUTE)
}
