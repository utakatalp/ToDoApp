package com.todoapp.mobile.data.source.remote.datasource.personal

import com.todoapp.mobile.data.model.network.data.PersonalTaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest

interface PersonalTaskRemoteDataSource {

    suspend fun createTask(taskRequest: TaskRequest): Result<PersonalTaskData>

    suspend fun updateTask(taskUpdateRequest: TaskUpdateRequest): Result<PersonalTaskData>

    suspend fun deleteTask(id: Long): Result<Unit>

    suspend fun getTasks(): Result<List<PersonalTaskData>>
}
