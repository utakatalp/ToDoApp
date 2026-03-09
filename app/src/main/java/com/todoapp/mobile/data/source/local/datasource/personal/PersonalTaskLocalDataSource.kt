package com.todoapp.mobile.data.source.local.datasource.personal

import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import kotlinx.coroutines.flow.Flow

interface PersonalTaskLocalDataSource {

    fun observeAll(): Flow<List<PersonalTaskEntity>>

    fun observeRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<PersonalTaskEntity>>

    fun observeByDate(date: Long): Flow<List<PersonalTaskEntity>>

    fun countInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int>

    fun observeCompletedCountsByDay(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DayCount>>

    suspend fun insert(task: PersonalTaskEntity)
    suspend fun insertAll(tasks: List<PersonalTaskEntity>)
    suspend fun update(task: PersonalTaskEntity)
    suspend fun delete(task: PersonalTaskEntity)
    suspend fun deleteAll()

    suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean)
    suspend fun getTaskById(id: Long): PersonalTaskEntity?

    suspend fun updateOrderIndex(id: Long, orderIndex: Int)
    suspend fun updateOrderIndices(orderUpdates: List<Pair<Long, Int>>)

    // --- Sync helpers ---
    suspend fun getByRemoteId(remoteId: Long): PersonalTaskEntity?
    suspend fun getPendingCreates(): List<PersonalTaskEntity>
    suspend fun getPendingUpdates(): List<PersonalTaskEntity>
    suspend fun getPendingDeletes(): List<PersonalTaskEntity>

    suspend fun updateSyncStatus(id: Long, status: SyncStatus)
    suspend fun markCreatedSynced(id: Long, remoteId: Long)
    suspend fun markUpdatedSynced(id: Long)
    suspend fun deleteById(id: Long)
}
