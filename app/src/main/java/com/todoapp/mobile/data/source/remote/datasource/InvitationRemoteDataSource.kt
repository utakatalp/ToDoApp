package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.InvitationData
import com.todoapp.mobile.data.model.network.data.InvitationListData
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

interface InvitationRemoteDataSource {
    suspend fun listMyPending(): Result<InvitationListData>
    suspend fun accept(id: Long): Result<InvitationData>
    suspend fun decline(id: Long): Result<InvitationData>
}

class InvitationRemoteDataSourceImpl @Inject constructor(
    private val api: ToDoApi,
) : InvitationRemoteDataSource {
    override suspend fun listMyPending(): Result<InvitationListData> = handleRequest { api.listMyInvitations() }

    override suspend fun accept(id: Long): Result<InvitationData> = handleRequest { api.acceptInvitation(id) }

    override suspend fun decline(id: Long): Result<InvitationData> = handleRequest { api.declineInvitation(id) }
}
