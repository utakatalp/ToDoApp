package com.todoapp.mobile.data.source.local.datasource.user

import com.todoapp.mobile.data.model.entity.user.UserEntity
import com.todoapp.mobile.data.source.local.dao.user.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserLocalDataSourceImpl @Inject constructor(
    private val dao: UserDao
) : UserLocalDataSource {

    override suspend fun upsert(user: UserEntity) {
        dao.upsert(user)
    }

    override suspend fun upsertAll(users: List<UserEntity>) {
        dao.upsertAll(users)
    }

    override suspend fun insertAll(users: List<UserEntity>) {
        dao.insertAll(users)
    }

    override fun observeById(userId: Long): Flow<UserEntity?> {
        return dao.observeById(userId)
    }

    override fun observeAll(): Flow<List<UserEntity>> {
        return dao.observeAll()
    }

    override suspend fun delete(user: UserEntity) {
        dao.delete(user)
    }

    override suspend fun deleteById(userId: Long) {
        dao.deleteById(userId)
    }

    override suspend fun clear() {
        dao.clear()
    }
}
