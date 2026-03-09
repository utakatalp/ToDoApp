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
}
