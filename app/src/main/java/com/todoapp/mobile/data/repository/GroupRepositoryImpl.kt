package com.todoapp.mobile.data.repository

import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.source.local.datasource.group.GroupsLocalDataSourceImpl
import com.todoapp.mobile.data.source.remote.datasource.GroupRemoteDataSource
import com.todoapp.mobile.domain.observer.ConnectivityObserver
import com.todoapp.mobile.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val groupLocalDataSource: GroupsLocalDataSourceImpl,
    private val connectivityObserver: ConnectivityObserver
) : GroupRepository {
    override suspend fun createGroup(request: CreateGroupRequest): Result<Unit> {
        val orderIndex = groupLocalDataSource.getOrderIndex() + 1

        if (!connectivityObserver.isConnected.value) {
            groupLocalDataSource.insert(
                request.toEntity(
                    orderIndex = orderIndex,
                    syncStatus = SyncStatus.PENDING_CREATE,
                )
            )
            return Result.success(Unit)
        }

        return groupRemoteDataSource.createGroup(request).fold(
            onSuccess = { createdGroup ->
                groupLocalDataSource.insert(createdGroup.toEntity(orderIndex))
                Result.success(Unit)
            },
            onFailure = { throwable ->
                groupLocalDataSource.insert(
                    request.toEntity(
                        orderIndex = orderIndex,
                        syncStatus = SyncStatus.PENDING_CREATE,
                    )
                )
                Result.failure(DomainException.fromThrowable(throwable))
            },
        )
    }

    override suspend fun getGroups(): Result<GroupSummaryDataList> {
        return groupRemoteDataSource.getGroups()
            .onSuccess {
                Result.success(it)
            }.onFailure {
                Result.failure<GroupSummaryDataList>(it)
            }
    }

    override suspend fun deleteGroup(id: Long): Result<Unit> {
        return groupRemoteDataSource.deleteGroup(id)
            .onSuccess {
                Result.success(it)
            }
            .onFailure {
                Result.failure<Unit>(it)
            }
    }
}
