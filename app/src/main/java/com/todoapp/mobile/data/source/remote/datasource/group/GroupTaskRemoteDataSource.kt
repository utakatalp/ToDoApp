package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest

interface GroupTaskRemoteDataSource {
    suspend fun createTask(taskRequest: TaskRequest): Result<GroupTaskData>
    suspend fun getTasks(groupId: Long): Result<List<GroupTaskData>>
    suspend fun updateTaskCompletion(taskId: Long): Result<GroupTaskData?>
    suspend fun deleteTask(taskId: Long): Result<Unit>
}
