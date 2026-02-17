package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.FamilyGroupData
import com.todoapp.mobile.data.model.network.data.FamilyGroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateFamilyGroupRequest

interface FamilyGroupRepository {
    suspend fun createFamilyGroup(request: CreateFamilyGroupRequest): Result<FamilyGroupData>

    suspend fun getFamilyGroups(): Result<FamilyGroupSummaryDataList>

    suspend fun deleteFamilyGroup(id: Long): Result<Unit>
}
