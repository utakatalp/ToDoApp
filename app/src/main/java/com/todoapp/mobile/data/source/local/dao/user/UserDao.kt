package com.todoapp.mobile.data.source.local.dao.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.todoapp.mobile.data.model.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Upsert
    suspend fun upsertAll(user: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun observeById(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY display_name ASC")
    fun observeAll(): Flow<List<UserEntity>>

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteById(userId: Long)

    @Query("DELETE FROM users")
    suspend fun clear()
}
