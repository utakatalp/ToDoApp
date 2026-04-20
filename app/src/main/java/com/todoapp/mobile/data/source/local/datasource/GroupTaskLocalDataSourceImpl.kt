package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupTaskEntity
import com.todoapp.mobile.data.source.local.GroupTaskDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GroupTaskLocalDataSourceImpl @Inject constructor(
    private val groupTaskDao: GroupTaskDao,
) : GroupTaskLocalDataSource {

    override fun observeByGroupId(localGroupId: Long): Flow<List<GroupTaskEntity>> =
        groupTaskDao.getTasksByGroupId(localGroupId)

    override fun searchAll(query: String): Flow<List<GroupTaskEntity>> =
        groupTaskDao.searchTasks(query)

    override suspend fun insert(task: GroupTaskEntity) = groupTaskDao.insert(task)

    override suspend fun insertAll(tasks: List<GroupTaskEntity>) = groupTaskDao.insertAll(tasks)

    override suspend fun update(task: GroupTaskEntity) = groupTaskDao.update(task)

    override suspend fun delete(task: GroupTaskEntity) = groupTaskDao.delete(task)

    override suspend fun deleteByGroupId(localGroupId: Long) =
        groupTaskDao.deleteByGroupId(localGroupId)

    override suspend fun deleteByRemoteId(remoteId: Long) =
        groupTaskDao.deleteByRemoteId(remoteId)

    override suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity? =
        groupTaskDao.getByRemoteId(remoteId)

    override suspend fun getAllRemoteIds(): List<Long> =
        groupTaskDao.getAllRemoteIds()

    override suspend fun updateCompletion(remoteId: Long, isCompleted: Boolean) =
        groupTaskDao.updateCompletion(remoteId, isCompleted)

    override suspend fun updateTask(
        remoteId: Long,
        title: String,
        description: String?,
        dueDate: Long?,
        priority: String?,
        assigneeUserId: Long?,
        assigneeDisplayName: String?,
        assigneeAvatarUrl: String?,
    ) = groupTaskDao.updateTask(
        remoteId = remoteId,
        title = title,
        description = description,
        dueDate = dueDate,
        priority = priority,
        assigneeUserId = assigneeUserId,
        assigneeDisplayName = assigneeDisplayName,
        assigneeAvatarUrl = assigneeAvatarUrl,
    )
}
