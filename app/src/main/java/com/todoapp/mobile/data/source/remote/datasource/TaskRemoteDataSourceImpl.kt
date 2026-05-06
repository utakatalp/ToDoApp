package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleEmptyRequest
import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.data.TaskListData
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toCreateTaskRequestDto
import javax.inject.Inject

class TaskRemoteDataSourceImpl
@Inject
constructor(
    private val todoApi: ToDoApi,
) : TaskRemoteDataSource {
    override suspend fun addTask(
        task: Task,
        familyGroupId: Long?,
        assignedToUserId: Long?,
        priority: String?,
    ): Result<TaskData> = handleRequest {
        todoApi.addTask(
            task.toCreateTaskRequestDto(familyGroupId, assignedToUserId, priority),
        )
    }

    override suspend fun updateTask(
        id: Long,
        task: Task,
        familyGroupId: Long?,
        assignedToUserId: Long?,
    ): Result<TaskData> = handleRequest {
        // Backend's PUT /tasks identifies the task by `body.id` (= server id).
        // Task.toCreateTaskRequestDto() carries Task.id which is the local Room PK,
        // so we override it here with the server id passed by callers.
        todoApi.updateTask(
            task
                .toCreateTaskRequestDto(familyGroupId = familyGroupId, assignedToUserId = assignedToUserId)
                .copy(id = id),
        )
    }

    override suspend fun deleteTask(id: Long): Result<Unit> = handleEmptyRequest { todoApi.deleteTask(id) }

    override suspend fun getTasks(familyGroupId: Long?): Result<TaskListData> = handleRequest {
        todoApi.getTasks(
            familyGroupId,
        )
    }
}
