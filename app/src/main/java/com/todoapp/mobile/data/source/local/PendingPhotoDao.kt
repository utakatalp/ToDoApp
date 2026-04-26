package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.PendingPhotoEntity

@Dao
interface PendingPhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PendingPhotoEntity): Long

    @Query("SELECT * FROM pending_photos WHERE local_task_id = :localTaskId ORDER BY created_at ASC")
    suspend fun getByLocalTaskId(localTaskId: Long): List<PendingPhotoEntity>

    @Query("DELETE FROM pending_photos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pending_photos WHERE local_task_id = :localTaskId")
    suspend fun deleteByLocalTaskId(localTaskId: Long)

    @Query("DELETE FROM pending_photos")
    suspend fun deleteAll()
}
