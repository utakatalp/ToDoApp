package com.todoapp.mobile.data.source.local.datasource

import com.todoapp.mobile.data.model.entity.GroupTaskEntity
import kotlinx.coroutines.flow.Flow

interface GroupTaskLocalDataSource {
    fun observeByGroupId(localGroupId: Long): Flow<List<GroupTaskEntity>>
    fun searchAll(query: String): Flow<List<GroupTaskEntity>>
    suspend fun insert(task: GroupTaskEntity)
    suspend fun insertAll(tasks: List<GroupTaskEntity>)
    suspend fun update(task: GroupTaskEntity)
    suspend fun delete(task: GroupTaskEntity)
    suspend fun deleteByGroupId(localGroupId: Long)
    suspend fun deleteByRemoteId(remoteId: Long)
    suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity?
    suspend fun updateCompletion(remoteId: Long, isCompleted: Boolean)
    suspend fun updateTask(
        remoteId: Long,
        title: String,
        description: String?,
        dueDate: Long?,
        priority: String?,
        assigneeUserId: Long?,
        assigneeDisplayName: String?,
        assigneeAvatarUrl: String?,
    )
}
