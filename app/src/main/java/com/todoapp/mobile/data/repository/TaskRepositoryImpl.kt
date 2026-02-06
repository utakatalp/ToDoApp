package com.todoapp.mobile.data.repository

import android.util.Log
import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSource
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toDomain
import com.todoapp.mobile.domain.repository.CompletedCountByDay
import com.todoapp.mobile.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val remoteDataSource: TaskRemoteDataSource,
    private val localDataSource: TaskLocalDataSource,
) : TaskRepository {
    override fun observeAll(): Flow<List<Task>> {
        return localDataSource.observeAll().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<Task>> {
        return localDataSource.observeRange(
            startDate = startDate.toEpochDay(),
            endDate = endDate.toEpochDay(),
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeTasksByDate(date: LocalDate): Flow<List<Task>> {
        return localDataSource.observeByDate(date = date.toEpochDay()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun countCompletedTasksInAWeek(
        date: LocalDate,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.countInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun countCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<CompletedCountByDay>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.observeCompletedCountsByDay(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
        )
            .map { rows: List<DayCount> ->
                rows.map { row ->
                    CompletedCountByDay(
                        date = LocalDate.ofEpochDay(row.date),
                        count = row.count
                    )
                }
            }
    }

    override fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return localDataSource.countInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun observePendingTasksYearToDate(date: LocalDate): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return localDataSource.countInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = false,
        )
    }

    override fun observePendingTasksInAWeek(
        date: LocalDate,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.countInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = false,
        )
    }

    override fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        return countCompletedCountsByDayInAWeek(date).map { dayCounts ->
            val map = dayCounts.associate { it.date to it.count }
            (0 until DAYS_IN_WEEK).map { dayOffset ->
                map[weekStart.plusDays(dayOffset.toLong())] ?: 0
            }
        }
    }

    override suspend fun insert(task: Task) {
        localDataSource.insert(task)

        // Never crash the app if the network is unavailable.
        runCatching {
            remoteDataSource.addTask(task)
        }.onFailure { throwable ->
            Log.d("TaskRepositoryImpl", "insert: remote addTask failed", throwable)
        }
    }

    override suspend fun delete(task: Task) {
        localDataSource.delete(task.toEntity())
    }

    override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        localDataSource.updateTaskCompletion(id, isCompleted)
    }

    override suspend fun getTaskById(id: Long): Task? = withContext(Dispatchers.IO) {
        localDataSource.getTaskById(id)?.toDomain()
    }

    override suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        localDataSource.update(task.toEntity())
    }

    override suspend fun syncLocalTasksToServer(): Result<Unit> = withContext(Dispatchers.IO) {
        // `remoteDataSource.addTask(...)` can throw (e.g., UnknownHostException) when there is no internet.
        // Handle both cases: (1) it throws, (2) it returns a Result.failure.
        val tasks = observeAll().first()
        Log.d("TaskRepositoryImpl", "syncLocalTasksToServer: $tasks")

        tasks.forEach { task ->
            val result = runCatching {
                remoteDataSource.addTask(task)
            }.getOrElse { throwable ->
                Log.d("TaskRepositoryImpl", "syncLocalTasksToServer: addTask threw", throwable)
                return@withContext Result.failure(throwable)
            }

            Log.d("TaskRepositoryImpl", "syncLocalTasksToServer: $result")

            if (result.isFailure) {
                val throwable = result.exceptionOrNull() ?: Exception("Failed to sync task")
                return@withContext Result.failure(throwable)
            }
        }

        Result.success(Unit)
    }

    companion object {
        private const val DAYS_TO_ADD = 6
        private const val DAYS_IN_WEEK = 7
    }
}
