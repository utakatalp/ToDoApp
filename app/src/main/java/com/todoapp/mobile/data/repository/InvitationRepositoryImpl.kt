package com.todoapp.mobile.data.repository

import com.todoapp.mobile.data.model.network.data.InvitationData
import com.todoapp.mobile.data.source.remote.datasource.InvitationRemoteDataSource
import com.todoapp.mobile.domain.model.Invitation
import com.todoapp.mobile.domain.repository.InvitationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvitationRepositoryImpl @Inject constructor(
    private val remote: InvitationRemoteDataSource,
) : InvitationRepository {
    private val _pending = MutableStateFlow<List<Invitation>>(emptyList())
    override val pending: StateFlow<List<Invitation>> = _pending.asStateFlow()

    @Volatile private var lastFetchedAt: Long = 0L

    override suspend fun refresh(force: Boolean): Result<Unit> {
        if (!force &&
            System.currentTimeMillis() - lastFetchedAt < CACHE_TTL_MS &&
            _pending.value.isNotEmpty()
        ) {
            return Result.success(Unit)
        }
        return remote.listMyPending().map { data ->
            _pending.value = data.items.map { it.toDomain() }
            lastFetchedAt = System.currentTimeMillis()
        }
    }

    override suspend fun accept(id: Long): Result<Long> {
        val target = _pending.value.firstOrNull { it.id == id }
        return remote.accept(id).map {
            _pending.update { list -> list.filterNot { it.id == id } }
            target?.groupId ?: it.groupId
        }
    }

    override suspend fun decline(id: Long): Result<Unit> = remote.decline(id).map {
        _pending.update { list -> list.filterNot { it.id == id } }
    }

    private fun InvitationData.toDomain(): Invitation = Invitation(
        id = id,
        groupId = groupId,
        groupName = groupName,
        groupAvatarUrl = groupAvatarUrl,
        inviterUserId = inviterUserId,
        inviterName = inviterName,
        inviterAvatarUrl = inviterAvatarUrl,
        inviteeEmail = inviteeEmail,
        createdAt = createdAt,
    )

    private companion object {
        const val CACHE_TTL_MS = 30_000L
    }
}
