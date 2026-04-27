package com.todoapp.mobile.domain.repository

import kotlinx.coroutines.flow.Flow

interface ActivityPreferences {
    fun observeIncludeRecurring(): Flow<Boolean>

    suspend fun setIncludeRecurring(value: Boolean)
}
