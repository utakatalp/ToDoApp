package com.todoapp.mobile.data.repository

import com.todoapp.mobile.domain.repository.ActivityPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityPreferencesImpl
@Inject
constructor(
    private val dataStoreHelper: DataStoreHelper,
) : ActivityPreferences {
    override fun observeIncludeRecurring(): Flow<Boolean> = dataStoreHelper
        .observeOptionalString(KEY_INCLUDE_RECURRING)
        .map { it == "true" }

    override suspend fun setIncludeRecurring(value: Boolean) {
        dataStoreHelper.saveString(KEY_INCLUDE_RECURRING, value.toString())
    }

    private companion object {
        const val KEY_INCLUDE_RECURRING = "activity_include_recurring"
    }
}
