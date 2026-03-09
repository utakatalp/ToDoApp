package com.todoapp.mobile.data.source.remote.datasource.group

import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList

interface GroupSummaryRemoteDataSource {

    suspend fun getGroups(): Result<GroupSummaryDataList>
}
