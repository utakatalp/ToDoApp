package com.todoapp.mobile.data.repository

import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.source.local.PomodoroDao
import com.todoapp.mobile.domain.model.Pomodoro
import com.todoapp.mobile.domain.repository.PomodoroRepository
import javax.inject.Inject

class PomodoroRepositoryImpl @Inject constructor(
    private val pomodoroDao: PomodoroDao
) : PomodoroRepository {
    override suspend fun getPomodoro(): Pomodoro? = pomodoroDao.getPomodoro()?.toDomain()
    override suspend fun updatePomodoro(pomodoro: Pomodoro) = pomodoroDao.updatePomodoro(pomodoro.toEntity())
    override suspend fun insertPomodoro(pomodoro: Pomodoro) = pomodoroDao.insertPomodoro(pomodoro.toEntity())
}
