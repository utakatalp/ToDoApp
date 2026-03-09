package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import javax.inject.Inject

class GroupSummaryRemoteDataSourceImpl @Inject constructor(
    private val todoApi: ToDoApi
) : GroupSummaryRemoteDataSource {

    override suspend fun getGroups(): Result<GroupSummaryDataList> {
        return handleRequest { todoApi.getGroups() }
    }
}
