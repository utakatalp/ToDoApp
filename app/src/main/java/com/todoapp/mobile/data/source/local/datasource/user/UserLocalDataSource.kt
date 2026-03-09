package com.todoapp.mobile.data.source.local.datasource.user

import com.todoapp.mobile.data.model.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    suspend fun upsert(user: UserEntity)

    suspend fun upsertAll(users: List<UserEntity>)

    suspend fun insertAll(users: List<UserEntity>)

    fun observeById(userId: Long): Flow<UserEntity?>

    fun observeAll(): Flow<List<UserEntity>>

    suspend fun delete(user: UserEntity)

    suspend fun deleteById(userId: Long)

    suspend fun clear()
}
