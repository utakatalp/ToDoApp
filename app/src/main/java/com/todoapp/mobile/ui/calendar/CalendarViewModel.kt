package com.todoapp.mobile.ui.calendar

import androidx.lifecycle.ViewModel
import com.todoapp.mobile.ui.calendar.CalendarContract.UiAction
import com.todoapp.mobile.ui.calendar.CalendarContract.UiEffect
import com.todoapp.mobile.ui.calendar.CalendarContract.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlin.getValue

class CalendarViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>() }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            is UiAction.OnFirstDateDeselect -> {
                _uiState.update { it.copy(selectedFirstDate = null) }
            }

            is UiAction.OnSecondDateDeselect -> {
                _uiState.update { it.copy(selectedSecondDate = null) }
            }

            is UiAction.OnFirstDateSelect -> {
                _uiState.update {
                    it.copy(selectedFirstDate = uiAction.date)
                }
            }

            is UiAction.OnSecondDateSelect -> {
                _uiState.update { it.copy(selectedSecondDate = uiAction.date) }
            }
        }
    }
}
