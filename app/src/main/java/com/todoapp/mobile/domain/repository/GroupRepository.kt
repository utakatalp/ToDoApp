package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest

interface GroupRepository {
    suspend fun createGroup(request: CreateGroupRequest): Result<Unit>

    suspend fun getGroups(): Result<GroupSummaryDataList>

    suspend fun deleteGroup(id: Long): Result<Unit>
}
