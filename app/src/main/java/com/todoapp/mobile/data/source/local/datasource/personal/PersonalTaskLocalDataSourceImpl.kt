package com.todoapp.mobile.data.source.local.datasource.personal

import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.dao.personal.PersonalTaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PersonalTaskLocalDataSourceImpl @Inject constructor(
    private val personalTaskDao: PersonalTaskDao,
) : PersonalTaskLocalDataSource {

    override fun observeAll(): Flow<List<PersonalTaskEntity>> = personalTaskDao.getAllTasks()

    override fun observeRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<PersonalTaskEntity>> = personalTaskDao.loadTasksBetweenRange(
        startDate = startDate,
        endDate = endDate,
    )

    override fun observeByDate(date: Long): Flow<List<PersonalTaskEntity>> =
        personalTaskDao.getTasksByDate(date = date)

    override fun countInRange(
        startDate: Long,
        endDate: Long,
        isCompleted: Boolean,
    ): Flow<Int> = personalTaskDao.getTaskCountInRange(
        startDate = startDate,
        endDate = endDate,
        isCompleted = isCompleted,
    )

    override fun observeCompletedCountsByDay(
        startDate: Long,
        endDate: Long,
    ): Flow<List<DayCount>> = personalTaskDao.observeCompletedCountsByDay(
        startDate = startDate,
        endDate = endDate,
    )

    override suspend fun insert(task: PersonalTaskEntity) {
        personalTaskDao.insert(task)
    }

    override suspend fun insertAll(tasks: List<PersonalTaskEntity>) {
        personalTaskDao.insertAll(tasks)
    }

    override suspend fun update(task: PersonalTaskEntity) {
        personalTaskDao.update(task)
    }

    override suspend fun delete(task: PersonalTaskEntity) {
        personalTaskDao.delete(task)
    }

    override suspend fun deleteAll() {
        val tasks = personalTaskDao.getAllTasks().first()
        personalTaskDao.deleteAll(tasks)
    }

    override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) {
        personalTaskDao.updateTaskCompletion(id = id, isCompleted = isCompleted)
    }

    override suspend fun getTaskById(id: Long): PersonalTaskEntity? = personalTaskDao.getTaskById(id)

    override suspend fun updateOrderIndex(id: Long, orderIndex: Int) {
        personalTaskDao.updateOrderIndex(id = id, orderIndex = orderIndex)
    }

    override suspend fun updateOrderIndices(orderUpdates: List<Pair<Long, Int>>) {
        for ((id, orderIndex) in orderUpdates) {
            personalTaskDao.updateOrderIndex(id = id, orderIndex = orderIndex)
        }
    }

    // --- Sync helpers ---

    override suspend fun getByRemoteId(remoteId: Long): PersonalTaskEntity? =
        personalTaskDao.getByRemoteId(remoteId)

    override suspend fun getPendingCreates(): List<PersonalTaskEntity> =
        personalTaskDao.getPendingCreates()

    override suspend fun getPendingUpdates(): List<PersonalTaskEntity> =
        personalTaskDao.getPendingUpdates()

    override suspend fun getPendingDeletes(): List<PersonalTaskEntity> =
        personalTaskDao.getPendingDeletes()

    override suspend fun updateSyncStatus(id: Long, status: SyncStatus) {
        personalTaskDao.updateSyncStatus(id = id, status = status)
    }

    override suspend fun markCreatedSynced(id: Long, remoteId: Long) {
        personalTaskDao.markCreatedSynced(id = id, remoteId = remoteId)
    }

    override suspend fun markUpdatedSynced(id: Long) {
        personalTaskDao.markUpdatedSynced(id = id)
    }

    override suspend fun deleteById(id: Long) {
        personalTaskDao.deleteById(id)
    }
}
