package com.todoapp.mobile.data.repository

import com.todoapp.mobile.domain.repository.DailyCardPosition
import com.todoapp.mobile.domain.repository.DailyPlanPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DailyPlanPreferencesImpl @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
) : DailyPlanPreferences {

    companion object {
        private const val PLAN_TIME_KEY = "daily_plan_time"
        private const val CARD_POSITION_X_KEY = "daily_plan_card_position_x"
        private const val CARD_POSITION_Y_KEY = "daily_plan_card_position_y"
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }

    override fun observePlanTime(): Flow<LocalTime?> {
        return dataStoreHelper.getOptionalString(PLAN_TIME_KEY).map { value ->
            if (value.isNullOrBlank()) {
                null
            } else {
                runCatching { LocalTime.parse(value, FORMATTER) }.getOrNull()
            }
        }
    }

    override suspend fun savePlanTime(time: LocalTime) {
        dataStoreHelper.saveString(PLAN_TIME_KEY, time.format(FORMATTER))
    }

    override fun observeCardPosition(): Flow<DailyCardPosition> {
        return combine(
            dataStoreHelper.getOptionalString(CARD_POSITION_X_KEY),
            dataStoreHelper.getOptionalString(CARD_POSITION_Y_KEY)
        ) { x, y ->
            DailyCardPosition(
                x?.toFloatOrNull() ?: 0f,
                y?.toFloatOrNull() ?: 0f
            )
        }
    }

    override suspend fun saveCardPosition(position: DailyCardPosition) {
        dataStoreHelper.saveString(CARD_POSITION_X_KEY, position.cardPositionX.toString())
        dataStoreHelper.saveString(CARD_POSITION_Y_KEY, position.cardPositionY.toString())
    }
}
