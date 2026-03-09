package com.todoapp.mobile.domain.repository.personal

import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PersonalTaskRepository {

    fun observeTasks(): Flow<List<Task.Personal>>

    fun observeTasksByDate(
        dateEpochDay: LocalDate
    ): Flow<List<Task.Personal>>

    fun observeRange(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate,
    ): Flow<List<Task.Personal>>

    fun observeCompletedTaskCount(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate,
    ): Flow<Int>

    fun observeNonCompletedTaskCount(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate,
    ): Flow<Int>

    fun observePendingTasksInAWeek(
        date: LocalDate
    ): Flow<Int>

    fun observeCompletedTasksInAWeek(
        date: LocalDate
    ): Flow<Int>

    suspend fun createTask(
        taskRequest: TaskRequest
    ): Result<Unit>
    fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>>

    suspend fun updateTask(
        task: Task.Personal
    ): Result<Unit>

    suspend fun deleteTask(
        task: Task.Personal
    ): Result<Unit>

    suspend fun updateTaskCompletion(
        id: Long,
        isCompleted: Boolean
    ): Result<Unit>

    suspend fun getTaskById(
        id: Long
    ): Task.Personal?

    suspend fun reorderTasksForDate(
        dateEpochDay: Long,
        fromIndex: Int,
        toIndex: Int
    ): Result<Unit>

    suspend fun deleteAllTasks(): Result<Unit>

    suspend fun syncLocalTasksToServer(): Result<Unit>

    suspend fun syncRemoteTasksToLocal(): Result<Unit>

    fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int>

    fun observePendingTasksYearToDate(date: LocalDate): Flow<Int>
}
