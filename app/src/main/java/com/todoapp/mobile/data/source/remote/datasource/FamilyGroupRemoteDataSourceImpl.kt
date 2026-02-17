package com.todoapp.mobile.data.source.remote.datasource

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.FamilyGroupData
import com.todoapp.mobile.data.model.network.data.FamilyGroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateFamilyGroupRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class FamilyGroupRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : FamilyGroupRemoteDataSource {
    override suspend fun createFamilyGroup(request: CreateFamilyGroupRequest): Result<FamilyGroupData> {
        return handleRequest { todoApi.createFamilyGroup(request) }
    }

    override suspend fun getFamilyGroups(): Result<FamilyGroupSummaryDataList> {
        return handleRequest { todoApi.getFamilyGroups() }
    }

    override suspend fun deleteFamilyGroup(id: Long): Result<Unit> {
        return handleRequest { todoApi.deleteFamilyGroup(id) }
    }
}
