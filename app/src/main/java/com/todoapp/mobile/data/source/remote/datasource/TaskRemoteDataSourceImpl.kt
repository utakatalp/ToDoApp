package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.mapper.toRequest
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.data.TaskListData
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.domain.model.Task
import javax.inject.Inject

class TaskRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : TaskRemoteDataSource {
    override suspend fun addTask(task: Task.Group, familyGroupId: Long?): Result<TaskData> {
        task.toRequest()
        return handleRequest { todoApi.addTask(task.toRequest()) }
    }

    override suspend fun updateTask(taskUpdateRequest: TaskUpdateRequest): Result<TaskData> {
        return handleRequest { todoApi.updateTask(taskUpdateRequest) }
    }

    override suspend fun deleteTask(id: Long): Result<Unit> {
        return handleRequest { todoApi.deleteTask(id) }
    }

    override suspend fun getTasks(familyGroupId: Long?): Result<TaskListData> {
        return handleRequest { todoApi.getTasks() }
    }
}
