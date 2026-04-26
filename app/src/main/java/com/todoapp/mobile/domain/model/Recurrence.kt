package com.todoapp.mobile.domain.model

import java.time.LocalDate

enum class Recurrence {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    ;

    companion object {
        fun fromStorage(value: String?): Recurrence = value?.let { runCatching { valueOf(it) }.getOrNull() } ?: NONE
    }
}

/**
 * Whether a task with this recurrence rule fires on [day], anchored at [anchor] (the date the
 * user chose when creating the task). Pure function — used by the alarm next-fire calculator
 * and the per-week stat-card expansion.
 */
fun Recurrence.firesOn(anchor: LocalDate, day: LocalDate): Boolean = when (this) {
    Recurrence.NONE -> anchor == day
    Recurrence.DAILY -> !day.isBefore(anchor)
    Recurrence.WEEKLY -> !day.isBefore(anchor) && day.dayOfWeek == anchor.dayOfWeek
    Recurrence.MONTHLY -> !day.isBefore(anchor) &&
        day.dayOfMonth == clampedDayOfMonth(anchor.dayOfMonth, day.year, day.monthValue)
    Recurrence.YEARLY -> !day.isBefore(anchor) &&
        day.monthValue == anchor.monthValue &&
        day.dayOfMonth == clampedDayOfMonth(anchor.dayOfMonth, day.year, day.monthValue)
}

/**
 * Returns the day-of-month to use when the anchor is later than the target month's length.
 * E.g. clampedDayOfMonth(31, 2026, 2) = 28 (Feb 2026 has 28 days). For yearly tasks anchored on
 * Feb 29, returns 28 in non-leap years.
 */
fun clampedDayOfMonth(anchorDayOfMonth: Int, year: Int, month: Int): Int {
    val maxDay = LocalDate.of(year, month, 1).lengthOfMonth()
    return minOf(anchorDayOfMonth, maxDay)
}
