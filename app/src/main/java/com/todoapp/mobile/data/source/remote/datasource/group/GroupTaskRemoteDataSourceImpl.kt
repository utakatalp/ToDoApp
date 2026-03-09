package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.model.network.data.toGroupTaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class GroupTaskRemoteDataSourceImpl @Inject constructor(private val todoApi: ToDoApi) : GroupTaskRemoteDataSource {
    override suspend fun createTask(taskRequest: TaskRequest): Result<GroupTaskData> {
        return handleRequest { todoApi.addTask(taskRequest) }.fold(
            onSuccess = { Result.success(it.toGroupTaskData()) },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun getTasks(groupId: Long): Result<List<GroupTaskData>> {
        return handleRequest {
            todoApi.getTasks(groupId)
        }.fold(
            onSuccess = { list ->
                Result.success(
                    list.tasks.map { item ->
                    item.toGroupTaskData()
                }
                )
            },
            onFailure = { Result.failure(it) }
        )
    }
}
