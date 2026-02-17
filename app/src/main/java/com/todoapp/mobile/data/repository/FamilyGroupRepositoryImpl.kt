package com.todoapp.mobile.data.repository

import com.todoapp.mobile.data.model.network.data.FamilyGroupData
import com.todoapp.mobile.data.model.network.data.FamilyGroupSummaryDataList
import com.todoapp.mobile.data.model.network.request.CreateFamilyGroupRequest
import com.todoapp.mobile.data.source.remote.datasource.FamilyGroupRemoteDataSource
import com.todoapp.mobile.domain.repository.FamilyGroupRepository
import javax.inject.Inject

class FamilyGroupRepositoryImpl @Inject constructor(
    private val familyGroupRemoteDataSource: FamilyGroupRemoteDataSource
) : FamilyGroupRepository {
    override suspend fun createFamilyGroup(request: CreateFamilyGroupRequest): Result<FamilyGroupData> {
        return familyGroupRemoteDataSource.createFamilyGroup(request)
            .onSuccess {
                Result.success(it)
            }.onFailure {
                Result.failure<FamilyGroupData>(it)
            }
    }

    override suspend fun getFamilyGroups(): Result<FamilyGroupSummaryDataList> {
        return familyGroupRemoteDataSource.getFamilyGroups()
            .onSuccess {
                Result.success(it)
            }.onFailure {
                Result.failure<FamilyGroupSummaryDataList>(it)
            }
    }

    override suspend fun deleteFamilyGroup(id: Long): Result<Unit> {
        return familyGroupRemoteDataSource.deleteFamilyGroup(id)
            .onSuccess {
                Result.success(it)
            }
            .onFailure {
                Result.failure<Unit>(it)
            }
    }
}
