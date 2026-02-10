package com.todoapp.mobile.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

data class DailyCardPosition(
    val cardPositionX: Float = 0f,
    val cardPositionY: Float = 0f,
)

interface DailyPlanPreferences {
    fun observePlanTime(): Flow<LocalTime?>
    suspend fun savePlanTime(time: LocalTime)
    fun observeCardPosition(): Flow<DailyCardPosition>
    suspend fun saveCardPosition(position: DailyCardPosition)
}
