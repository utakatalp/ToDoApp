package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class CompletedCountByDay(
    val date: LocalDate,
    val count: Int
)

interface TaskRepository {
    fun observeAll(): Flow<List<Task>>
    fun observeRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>
    fun observeTasksByDate(date: LocalDate): Flow<List<Task>>
    fun countCompletedTasksInAWeek(date: LocalDate): Flow<Int>
    fun countCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<CompletedCountByDay>>
    fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>>
    fun observePendingTasksInAWeek(date: LocalDate): Flow<Int>
    fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int>
    fun observePendingTasksYearToDate(date: LocalDate): Flow<Int>
    suspend fun insert(task: Task)
    suspend fun delete(task: Task)
    suspend fun updateTask(id: Long, isCompleted: Boolean)
}
