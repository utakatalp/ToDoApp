package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    suspend fun observeRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
    fun observeTasksByDate(date: LocalDate): Flow<List<Task>>
    fun observeCompletedTasksInAWeek(date: LocalDate): Flow<Int>
    fun observePendingTasksInAWeek(date: LocalDate): Flow<Int>
    suspend fun insert(task: Task)
    suspend fun delete(task: Task)
    suspend fun updateTask(id: Long, isCompleted: Boolean)
}
