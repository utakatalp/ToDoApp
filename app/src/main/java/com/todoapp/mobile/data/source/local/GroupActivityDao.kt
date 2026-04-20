package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.GroupActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupActivityDao {

    @Query("SELECT * FROM group_activities WHERE local_group_id = :localGroupId ORDER BY timestamp DESC")
    fun getActivitiesByGroupId(localGroupId: Long): Flow<List<GroupActivityEntity>>

    @Query("SELECT * FROM group_activities WHERE local_group_id = :localGroupId ORDER BY timestamp DESC")
    suspend fun getActivitiesByGroupIdOnce(localGroupId: Long): List<GroupActivityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<GroupActivityEntity>)

    @Query("DELETE FROM group_activities WHERE local_group_id = :localGroupId")
    suspend fun deleteByGroupId(localGroupId: Long)
}
