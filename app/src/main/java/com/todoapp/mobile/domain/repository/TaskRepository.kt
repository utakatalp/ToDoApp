package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class CompletedCountByDay(
    val date: LocalDate,
    val count: Int,
)

interface TaskRepository {
    /** Map of remote task id -> list of photo URLs. Populated on every remote sync. */
    fun observeTaskPhotoUrls(): Flow<Map<Long, List<String>>>

    fun observeRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<Task>>

    fun observeTasksByDate(date: LocalDate): Flow<List<Task>>

    fun countCompletedTasksInAWeek(date: LocalDate): Flow<Int>

    fun countCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<CompletedCountByDay>>

    fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>>

    fun observePendingCountsByDayInAWeek(date: LocalDate): Flow<List<Int>>

    fun observePendingTasksInAWeek(date: LocalDate): Flow<Int>

    fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int>

    fun observePendingTasksYearToDate(date: LocalDate): Flow<Int>

    suspend fun insert(task: Task)

    suspend fun insertWithPhotos(
        task: Task,
        photos: List<Pair<ByteArray, String>>,
    ): Result<Unit>

    suspend fun delete(task: Task)

    suspend fun updateTaskCompletion(
        id: Long,
        isCompleted: Boolean,
    )

    suspend fun getTaskById(id: Long): Task?

    suspend fun fetchRemoteTask(id: Long): Result<Task>

    suspend fun uploadTaskPhoto(
        taskId: Long,
        bytes: ByteArray,
        mimeType: String,
    ): Result<String>

    suspend fun deleteTaskPhoto(
        taskId: Long,
        photoId: Long,
    ): Result<Unit>

    suspend fun update(task: Task)

    suspend fun syncRemoteTasksWithLocal(): Result<Unit>

    suspend fun syncLocalTasksToServer(): Result<Unit>

    suspend fun findNonSyncedTasks(): List<TaskEntity>

    fun observeAllTaskEntities(): Flow<List<TaskEntity>>

    fun observeAllTasks(): Flow<List<Task>>

    suspend fun syncTask(taskEntity: TaskEntity): Result<Unit>

    suspend fun deleteAllTasks(): Result<Unit>

    suspend fun getAllTasks(): Result<Unit>

    suspend fun reorderTasksForDate(
        date: LocalDate,
        fromIndex: Int,
        toIndex: Int,
    ): Result<Unit>

    fun observeTasksByWeekAndStatus(
        date: LocalDate,
        isCompleted: Boolean,
    ): Flow<List<Task>>

    fun searchTasks(query: String): Flow<List<Task>>
}
