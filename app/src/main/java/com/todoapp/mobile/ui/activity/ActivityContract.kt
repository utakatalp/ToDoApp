package com.todoapp.mobile.ui.activity

import java.time.LocalDate

object ActivityContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedDate: LocalDate = LocalDate.now(),
            val weeklyProgress: Float = 0f,
            val weeklyPendingProgress: Float = 0f,
            val weeklyBarValues: List<Int> = emptyList(),
            val yearlyProgress: Float = 0f,
            val yearlyPendingProgress: Float = 0f,
        ) : UiState

        data class Error(
            val message: String,
            val throwable: Throwable? = null,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnRetry : UiAction
    }

    sealed interface UiEffect
}
