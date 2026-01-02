package com.todoapp.mobile.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String?,
    val date: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val isCompleted: Boolean,
)
