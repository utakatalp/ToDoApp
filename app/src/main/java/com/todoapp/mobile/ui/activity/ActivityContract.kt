package com.todoapp.mobile.ui.activity

import java.time.LocalDate

object ActivityContract {
    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val selectedDate: LocalDate,
            val weeklyProgress: Float,
            val weeklyPendingProgress: Float,
            val weeklyBarValues: List<Int>,
            val yearlyProgress: Float,
            val yearlyPendingProgress: Float,
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
