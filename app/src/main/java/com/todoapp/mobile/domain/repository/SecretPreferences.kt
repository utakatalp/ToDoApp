package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.security.SecretModeEndCondition
import com.todoapp.mobile.domain.security.SecretModeReopenOptionId
import kotlinx.coroutines.flow.Flow

interface SecretPreferences {
    suspend fun saveCondition(condition: SecretModeEndCondition)
    suspend fun getCondition(): SecretModeEndCondition
    fun observeCondition(): Flow<SecretModeEndCondition>
    suspend fun setLastSelectedOptionId(id: SecretModeReopenOptionId)
    suspend fun getLastSelectedOptionId(): SecretModeReopenOptionId
}
