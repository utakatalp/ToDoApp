package com.todoapp.mobile.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.todoapp.mobile.data.model.entity.GroupMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupMemberDao {

    @Query("SELECT * FROM group_members WHERE local_group_id = :localGroupId")
    fun getMembersByGroupId(localGroupId: Long): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM group_members WHERE local_group_id = :localGroupId")
    suspend fun getMembersByGroupIdOnce(localGroupId: Long): List<GroupMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<GroupMemberEntity>)

    @Query("DELETE FROM group_members WHERE local_group_id = :localGroupId")
    suspend fun deleteByGroupId(localGroupId: Long)
}
