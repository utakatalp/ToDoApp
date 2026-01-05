package com.todoapp.mobile.ui.activity

import java.time.LocalDate

object ActivityContract {
    data class UiState(
        val selectedDate: LocalDate = LocalDate.now(),
        val weeklyProgress: Float = 0f,
        val weeklyPendingProgress: Float = 0f,
        val weeklyBarValues: List<Int> = emptyList(),
        val yearlyProgress: Float = 0f,
        val yearlyPendingProgress: Float = 0f,
    )

    sealed interface UiAction
    sealed interface UiEffect
}
