package com.todoapp.mobile.domain.repository.group

import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface GroupTaskRepository {

    suspend fun createTask(task: Task.Group): Result<GroupTaskData>

    fun observeTasks(groupId: Long): Flow<List<Task.Group>>

    fun filterTasks(groupId: Long, userId: Long): Flow<List<Task.Group>>

    suspend fun syncRemoteTasksToLocal(groupId: Long): Result<Unit>

    suspend fun updateTaskCompletion(taskId: Long): Result<Unit>

    suspend fun deleteTask(taskId: Long, taskRemoteId: Long?): Result<Unit>
}
