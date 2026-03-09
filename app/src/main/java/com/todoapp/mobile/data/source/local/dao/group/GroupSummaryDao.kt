package com.todoapp.mobile.data.source.local.dao.group

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.todoapp.mobile.data.model.entity.group.GroupSummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupSummaryDao {

    @Query("SELECT * FROM group_summaries ORDER BY order_index ASC")
    fun observeGroupSummaries(): Flow<List<GroupSummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupSummary: GroupSummaryEntity)

    @Insert
    suspend fun insertAll(groupSummaries: List<GroupSummaryEntity>)

    @Query("SELECT * FROM group_summaries WHERE id = :id")
    suspend fun getGroupSummaryById(id: Long): GroupSummaryEntity?

    @Query("DELETE FROM group_summaries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Update
    suspend fun update(groupSummary: GroupSummaryEntity)

    @Query("SELECT member_count FROM group_summaries WHERE remote_id = :id")
    fun observeUserCount(id: Long): Flow<Int>

    @Query("DELETE FROM group_summaries")
    suspend fun clear()
}
