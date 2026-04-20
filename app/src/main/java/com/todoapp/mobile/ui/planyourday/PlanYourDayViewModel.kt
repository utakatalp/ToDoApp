package com.todoapp.mobile.ui.planyourday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.alarm.buildDailyPlanAlarmItem
import com.todoapp.mobile.domain.repository.DailyPlanPreferences
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.planyourday.PlanYourDayContract.UiAction
import com.todoapp.mobile.ui.planyourday.PlanYourDayContract.UiEffect
import com.todoapp.mobile.ui.planyourday.PlanYourDayContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

private val DEFAULT_TIME: LocalTime = LocalTime.of(9, 0)

@HiltViewModel
class PlanYourDayViewModel @Inject constructor(
    private val dailyPlanPreferences: DailyPlanPreferences,
    private val alarmScheduler: AlarmScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<UiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private val _navEffect = Channel<NavigationEffect>()
    val navEffect = _navEffect.receiveAsFlow()

    init {
        observeData()
    }

    fun onAction(action: UiAction) {
        when (action) {
            is UiAction.OnHourChange -> {
                _uiState.update {
                    it.copy(selectedTime = it.selectedTime.withHour(action.hour))
                }
            }

            is UiAction.OnMinuteChange -> {
                _uiState.update {
                    it.copy(selectedTime = it.selectedTime.withMinute(action.minute))
                }
            }

            is UiAction.OnSave -> saveTime()
            is UiAction.OnCancel -> cancelChanges()
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            dailyPlanPreferences.observePlanTime().onEach { savedTime ->
                val time = savedTime ?: DEFAULT_TIME
                _uiState.update {
                    it.copy(
                        selectedTime = time,
                        savedTime = savedTime,
                    )
                }
            }.collect {}
        }
    }

    private fun saveTime() {
        viewModelScope.launch {
            val time = _uiState.value.selectedTime
            dailyPlanPreferences.savePlanTime(time)
            scheduleDailyPlanAlarm(time)
            _uiEffect.send(UiEffect.ShowToast(R.string.plan_time_saved))
            _uiEffect.send(UiEffect.NavigateBack)
        }
    }

    private fun scheduleDailyPlanAlarm(time: LocalTime) {
        val now = LocalDateTime.now()
        val alarmItem = buildDailyPlanAlarmItem(
            selectedTime = time,
            now = now,
            message = "",
        )
        alarmScheduler.schedule(alarmItem, AlarmType.DAILY_PLAN)
    }

    private fun cancelChanges() {
        val state = _uiState.value
        if (state.hasChanges) {
            val fallback = state.savedTime ?: DEFAULT_TIME
            _uiState.update { it.copy(selectedTime = fallback) }
            viewModelScope.launch {
                _uiEffect.send(UiEffect.ShowToast(R.string.changes_cancelled))
            }
        } else {
            viewModelScope.launch {
                _uiEffect.send(UiEffect.NavigateBack)
            }
        }
    }
}
