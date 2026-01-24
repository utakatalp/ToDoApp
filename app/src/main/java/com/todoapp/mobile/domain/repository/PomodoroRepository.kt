package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.Pomodoro

interface PomodoroRepository {
    suspend fun getPomodoro(): Pomodoro?
    suspend fun updatePomodoro(pomodoro: Pomodoro)
    suspend fun insertPomodoro(pomodoro: Pomodoro)
}
