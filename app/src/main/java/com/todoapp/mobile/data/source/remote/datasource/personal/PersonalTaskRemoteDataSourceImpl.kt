package com.todoapp.mobile.data.source.remote.datasource.personal

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.PersonalTaskData
import com.todoapp.mobile.data.model.network.data.toPersonalTaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class PersonalTaskRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : PersonalTaskRemoteDataSource {
    override suspend fun createTask(taskRequest: TaskRequest): Result<PersonalTaskData> {
        return handleRequest { todoApi.addTask(taskRequest) }
            .map { it.toPersonalTaskData() }
    }

    override suspend fun updateTask(taskUpdateRequest: TaskUpdateRequest): Result<PersonalTaskData> {
        return handleRequest { todoApi.updateTask(taskUpdateRequest) }
            .map { it.toPersonalTaskData() }
    }

    override suspend fun deleteTask(id: Long): Result<Unit> {
        return handleRequest { todoApi.deleteTask(id) }
    }

    override suspend fun getTasks(): Result<List<PersonalTaskData>> {
        return handleRequest { todoApi.getTasks() }
            .map { it.tasks.map { taskData -> taskData.toPersonalTaskData() } }
    }
}
