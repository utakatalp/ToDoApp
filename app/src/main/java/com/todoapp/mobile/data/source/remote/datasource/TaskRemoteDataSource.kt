package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.data.TaskListData
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest
import com.todoapp.mobile.domain.model.Task

interface TaskRemoteDataSource {
    suspend fun addTask(task: Task.Group, familyGroupId: Long?): Result<TaskData>

    suspend fun updateTask(taskUpdateRequest: TaskUpdateRequest): Result<TaskData>

    suspend fun deleteTask(id: Long): Result<Unit>

    suspend fun getTasks(familyGroupId: Long?): Result<TaskListData>
}
