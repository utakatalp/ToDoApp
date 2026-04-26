package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.Invitation
import kotlinx.coroutines.flow.StateFlow

interface InvitationRepository {
    val pending: StateFlow<List<Invitation>>

    suspend fun refresh(force: Boolean = false): Result<Unit>

    suspend fun accept(id: Long): Result<Long>

    suspend fun decline(id: Long): Result<Unit>
}
