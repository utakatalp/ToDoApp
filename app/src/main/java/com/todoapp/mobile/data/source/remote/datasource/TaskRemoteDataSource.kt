package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.domain.model.Task

interface TaskRemoteDataSource {
    suspend fun addTask(task: Task): Result<TaskData>

    suspend fun updateTask(id: Long): Result<TaskData>

    suspend fun deleteTask(id: Long): Result<Unit>
}
