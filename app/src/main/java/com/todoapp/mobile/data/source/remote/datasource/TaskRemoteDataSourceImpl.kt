package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toCreateTaskRequestDto
import javax.inject.Inject

class TaskRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : TaskRemoteDataSource {
    override suspend fun addTask(task: Task): Result<TaskData> {
        return handleRequest { todoApi.addTask(task.toCreateTaskRequestDto()) }
    }
}
