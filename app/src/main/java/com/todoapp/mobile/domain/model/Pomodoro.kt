package com.todoapp.mobile.domain.model

data class Pomodoro(
    val id: Long,
    val sessionCount: Int,
    val focusTime: Int,
    val shortBreak: Int,
    val longBreak: Int,
    val sectionCount: Int
)
