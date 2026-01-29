package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.PomodoroEntity
import com.todoapp.mobile.domain.model.Pomodoro

fun PomodoroEntity.toDomain(): Pomodoro =
    Pomodoro(
        id = id,
        sessionCount = sessionCount,
        focusTime = focusTime,
        shortBreak = shortBreak,
        longBreak = longBreak,
        sectionCount = sectionCount
    )

fun Pomodoro.toEntity(): PomodoroEntity =
    PomodoroEntity(
        id = id,
        sessionCount = sessionCount,
        focusTime = focusTime,
        shortBreak = shortBreak,
        longBreak = longBreak,
        sectionCount = sectionCount
    )
