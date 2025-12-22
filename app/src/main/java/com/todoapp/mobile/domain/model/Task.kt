package com.todoapp.mobile.domain.model

import java.time.LocalDate

data class Task(
    val taskId: String,
    val text: String,
    val isDone: Boolean,
    val date: LocalDate,
)
