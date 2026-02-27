package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {
    fun observeAll(): Flow<List<TaskEntity>>

    fun observeRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<TaskEntity>>

    fun observeByDate(date: Long): Flow<List<TaskEntity>>

    fun countInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int>

    fun observeCompletedCountsByDay(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DayCount>>

    suspend fun insert(task: TaskEntity)

    suspend fun delete(task: TaskEntity)

    suspend fun update(task: TaskEntity)

    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)

    suspend fun getTaskById(id: Long): TaskEntity?

    suspend fun deleteAll()

    suspend fun insertAll(tasks: List<TaskEntity>)
    suspend fun updateOrderIndex(id: Long, orderIndex: Int)
    suspend fun updateOrderIndices(orderUpdates: List<Pair<Long, Int>>)
}
