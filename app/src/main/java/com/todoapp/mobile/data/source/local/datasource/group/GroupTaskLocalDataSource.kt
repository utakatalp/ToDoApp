package com.todoapp.mobile.data.source.local.datasource.group

import com.todoapp.mobile.data.model.entity.group.GroupTaskEntity
import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface GroupTaskLocalDataSource {

    suspend fun insert(taskEntity: GroupTaskEntity)
    fun observeTasks(groupId: Long): Flow<List<Task.Group>>
    suspend fun getTaskCount(groupId: Long): Int
    suspend fun getByRemoteId(remoteId: Long): GroupTaskEntity?
    fun filterTasks(groupId: Long, userId: Long): Flow<List<Task.Group>>
}
