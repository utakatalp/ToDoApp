package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TaskLocalDataSourceImpl @Inject constructor(
    private val taskDao: TaskDao,
) : TaskLocalDataSource {
    override fun observeAll(): Flow<List<TaskEntity>> = taskDao.getAllTasks()

    override fun observeRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<TaskEntity>> = taskDao.loadTasksBetweenRange(startDate, endDate)

    override fun observeByDate(date: Long): Flow<List<TaskEntity>> = taskDao.getTasksByDate(date = date)

    override fun countInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int> = taskDao.getTaskCountInRange(
        startDate = startDate,
        endDate = endDate,
        isCompleted = isCompleted,
    )

    override fun observeCompletedCountsByDay(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DayCount>> = taskDao.observeCompletedCountsByDay(startDate, endDate)

    override suspend fun insert(task: TaskEntity) {
        taskDao.insert(task)
    }

    override suspend fun delete(task: TaskEntity) {
        taskDao.delete(task)
    }

    override suspend fun update(task: TaskEntity) {
        taskDao.update(task)
    }

    override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) {
        taskDao.updateTask(id, isCompleted)
    }

    override suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    override suspend fun deleteAll() {
        val tasks = taskDao.getAllTasks().first()
        taskDao.deleteAll(tasks)
    }

    override suspend fun insertAll(tasks: List<TaskEntity>) {
        taskDao.insertAll(tasks)
    }
    override suspend fun updateOrderIndex(id: Long, orderIndex: Int) {
        taskDao.updateOrderIndex(id = id, orderIndex = orderIndex)
    }

    override suspend fun updateOrderIndices(orderUpdates: List<Pair<Long, Int>>) {
        for ((id, orderIndex) in orderUpdates) {
            taskDao.updateOrderIndex(id = id, orderIndex = orderIndex)
        }
    }
}
