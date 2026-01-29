package com.todoapp.mobile.data.repository

import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.CompletedCountByDay
import com.todoapp.mobile.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
) : TaskRepository {
    override fun observeAll(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<Task>> {
        return taskDao.loadTasksBetweenRange(
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeTasksByDate(date: LocalDate): Flow<List<Task>> {
        return taskDao.getTasksByDate(date = date.toEpochDay()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun countCompletedTasksInAWeek(
        date: LocalDate,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return taskDao.getTaskCountInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun countCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<CompletedCountByDay>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return taskDao.observeCompletedCountsByDay(weekStart.toEpochDay(), weekEnd.toEpochDay())
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
        return taskDao.getTaskCountInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun observePendingTasksYearToDate(date: LocalDate): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return taskDao.getTaskCountInRange(
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
        return taskDao.getTaskCountInRange(
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
        taskDao.insert(task.toEntity())
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateTask(id, isCompleted)
    }

    override suspend fun getTaskById(id: Long): Task? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        taskDao.update(task.toEntity())
    override suspend fun updateTask(
        id: Long,
        isCompleted: Boolean,
    ) = withContext(Dispatchers.IO) {
        taskDao.updateTask(id, isCompleted)
    }

    companion object {
        private const val DAYS_TO_ADD = 6
        private const val DAYS_IN_WEEK = 7
    }
}
