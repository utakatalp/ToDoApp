package com.todoapp.mobile.data.repository

import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.model.entity.GroupEntity
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.source.local.datasource.GroupLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.GroupRemoteDataSource
import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.repository.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val groupLocalDataSource: GroupLocalDataSource,
) : GroupRepository {

    override suspend fun createGroup(request: CreateGroupRequest): Result<GroupData> {
        return groupRemoteDataSource.createGroup(request)
            .onSuccess { remote ->
                val entity = remote.toEntity()
                groupLocalDataSource.insert(withInitializedOrder(entity))
            }
    }

    override suspend fun getGroups(): Result<GroupSummaryDataList> {
        return groupRemoteDataSource.getGroups()
            .onSuccess { result ->
                val entities = result.groups.map { summary ->
                    summary.toEntity()
                }
                syncRemoteGroupsWithLocal(entities)
            }
    }

    override suspend fun deleteGroup(id: Long): Result<Unit> {
        val localEntity =
            groupLocalDataSource.getGroupById(id) ?: return Result.failure(Exception("Group not found"))
        return groupRemoteDataSource.deleteGroup(localEntity.remoteId!!)
            .onSuccess {
                groupLocalDataSource.delete(localEntity)
            }
    }

    override fun observeAllGroups(): Flow<List<Group>> {
        return groupLocalDataSource.getAllGroupsOrdered().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun reorderGroups(
        fromIndex: Int,
        toIndex: Int,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (fromIndex == toIndex) return@runCatching

            val current = groupLocalDataSource.getAllGroupsOrdered().first()

            if (fromIndex !in current.indices || toIndex !in current.indices) {
                return@runCatching
            }

            val reordered = current.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }

            val start = minOf(fromIndex, toIndex)
            val end = maxOf(fromIndex, toIndex)

            val updates = (start..end).map { index ->
                reordered[index].id to index
            }

            groupLocalDataSource.updateOrderIndices(updates)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) }
        )
    }

    private suspend fun syncRemoteGroupsWithLocal(remoteEntities: List<GroupEntity>) {
        val local = groupLocalDataSource.getAllGroupsOrdered().first()

        remoteEntities.forEach { remote ->
            val existing = local.find { it.remoteId == remote.remoteId }
            if (existing == null) {
                groupLocalDataSource.insert(withInitializedOrder(remote))
            } else {
                groupLocalDataSource.update(
                    existing.copy(
                        name = remote.name,
                        description = remote.description,
                        role = remote.role,
                        memberCount = remote.memberCount,
                        pendingTaskCount = remote.pendingTaskCount,
                        createdAt = remote.createdAt
                    )
                )
            }
        }
    }

    private suspend fun withInitializedOrder(entity: GroupEntity): GroupEntity {
        return if (entity.orderIndex != 0) {
            entity
        } else {
            val current = groupLocalDataSource.getAllGroupsOrdered().first()
            val nextIndex = (current.maxOfOrNull { it.orderIndex } ?: -1) + 1
            entity.copy(orderIndex = nextIndex)
        }
    }

    private fun GroupData.toEntity(): GroupEntity =
        GroupEntity(
            remoteId = id,
            name = name,
            description = description,
            createdAt = createdAt,
        )

    private fun GroupSummaryData.toEntity(): GroupEntity =
        GroupEntity(
            remoteId = id,
            name = name,
            description = description,
            createdAt = createdAt,
            role = role,
            memberCount = memberCount,
            pendingTaskCount = pendingTaskCount,
        )
}
