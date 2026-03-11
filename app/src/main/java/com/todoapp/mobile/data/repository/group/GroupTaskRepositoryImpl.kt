package com.todoapp.mobile.data.repository.group

import android.util.Log
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.mapper.toRequest
import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.source.local.datasource.group.GroupTaskLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.group.GroupTaskRemoteDataSource
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.group.GroupTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupTaskRepositoryImpl @Inject constructor(
    private val groupTaskRemoteDataSource: GroupTaskRemoteDataSource,
    private val groupTaskLocalDataSource: GroupTaskLocalDataSource,
) : GroupTaskRepository {
    companion object {
        private const val TAG = "GroupTaskRepository"
    }

    override suspend fun createTask(task: Task.Group): Result<GroupTaskData> {
        val orderIndex = groupTaskLocalDataSource.getTaskCount(task.groupId)

        val result = groupTaskRemoteDataSource.createTask(task.toRequest())

        return result
            .onSuccess {
                groupTaskLocalDataSource.insert(it.toEntity(groupId = task.groupId, orderIndex = orderIndex))
                return Result.success(it)
            }.onFailure {
                groupTaskLocalDataSource.insert(task.toEntity(orderIndex))
                return Result.failure(it)
            }
    }

    override fun observeTasks(groupId: Long): Flow<List<Task.Group>> {
        return groupTaskLocalDataSource.observeTasks(groupId)
    }

    override fun filterTasks(
        groupId: Long,
        userId: Long
    ): Flow<List<Task.Group>> {
        return groupTaskLocalDataSource.filterTasks(groupId, userId)
    }

    override suspend fun syncRemoteTasksToLocal(groupId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remoteTasks = groupTaskRemoteDataSource.getTasks(groupId).getOrThrow()

            var nextOrderIndex = groupTaskLocalDataSource.getTaskCount(groupId)

            for (remoteTask in remoteTasks) {
                val existing = groupTaskLocalDataSource.getByRemoteId(remoteTask.id)
                if (existing == null) {
                    groupTaskLocalDataSource.insert(
                        remoteTask.toEntity(
                            groupId = groupId,
                            orderIndex = nextOrderIndex,
                        ),
                    )
                    nextOrderIndex++
                }
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t ->
                Log.e(TAG, "syncRemoteTasksToLocal failed", t)
                Result.failure(DomainException.fromThrowable(t))
            },
        )
    }

    override suspend fun updateTaskCompletion(taskId: Long): Result<Unit> {
        Log.d(TAG, "updateTaskCompletion() called with taskId=$taskId")

        return groupTaskRemoteDataSource.updateTaskCompletion(taskId)
            .fold(
                onSuccess = { response ->
                    Log.d(TAG, "updateTaskCompletion() remote success, response=$response")
                    val isCompleted = response!!.isCompleted
                    Log.d(TAG, "updateTaskCompletion() parsed isCompleted=$isCompleted for taskId=$taskId")
                    groupTaskLocalDataSource.updateTaskCompletion(taskId, isCompleted)
                    Log.d(TAG, "updateTaskCompletion() local update done for taskId=$taskId")
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    Log.e(TAG, "updateTaskCompletion() failed for taskId=$taskId", throwable)
                    Result.failure(DomainException.fromThrowable(throwable))
                },
            )
    }

    override suspend fun deleteTask(taskId: Long, taskRemoteId: Long?): Result<Unit> {
        Log.d(TAG, "deleteTask() called with taskId=$taskId")
        if (taskRemoteId != null) {
            return groupTaskRemoteDataSource.deleteTask(taskRemoteId).fold(
                onSuccess = {
                    Log.d(TAG, "deleteTask() remote success for taskId=$taskRemoteId")
                    groupTaskLocalDataSource.deleteTask(taskId)
                    Log.d(TAG, "deleteTask() local delete done for taskId=$taskId")
                    Result.success(Unit)
                },
                onFailure = { throwable ->
                    Log.e(TAG, "deleteTask() failed for taskId=$taskId", throwable)
                    Result.failure(DomainException.fromThrowable(throwable))
                },
            )
        }
        groupTaskLocalDataSource.deleteTask(taskId)
        Log.d(TAG, "deleteTask() local delete done for taskId=$taskId")
        return Result.failure(DomainException.Server("Task remote id is null"))
    }
}
