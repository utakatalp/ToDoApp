package com.todoapp.mobile.ui.banner

import androidx.compose.runtime.Immutable
import com.todoapp.mobile.domain.engine.PomodoroMode

object BannerContract {
    @Immutable
    data class UiState(
        val minutes: Int? = null,
        val seconds: Int? = null,
        val isBannerActivated: Boolean = false,
        val isOverTime: Boolean? = null,
        val isVisible: Boolean = true,
        val mode: PomodoroMode = PomodoroMode.Focus,
    )

    sealed interface UiEffect {
        data object SessionFinished : UiEffect
    }

    sealed interface UiAction {
        data object OnBannerTap : UiAction
    }
}
