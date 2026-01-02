package com.todoapp.mobile.data.repository

import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.source.local.TaskDao
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override fun observeAll(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRange(
        startDate: LocalDate,
        endDate: LocalDate
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

    override fun observeCompletedTasksInAWeek(
        date: LocalDate
    ): Flow<Int> {
        val prevMonday = date.with(DayOfWeek.MONDAY)
        return taskDao.getCompletedTaskAmountInAWeek(prevMonday.toEpochDay(), date.toEpochDay())
    }

    override fun observePendingTasksInAWeek(
        date: LocalDate
    ): Flow<Int> {
        val prevMonday = date.with(DayOfWeek.MONDAY)
        return taskDao.getPendingTaskAmountInAWeek(prevMonday.toEpochDay(), date.toEpochDay())
    }

    override suspend fun insert(task: Task) {
        taskDao.insert(task.toEntity())
    }

    override suspend fun delete(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun updateTask(
        id: Long,
        isCompleted: Boolean
    ) = withContext(Dispatchers.IO) {
        taskDao.updateTask(id, isCompleted)
    }
}
