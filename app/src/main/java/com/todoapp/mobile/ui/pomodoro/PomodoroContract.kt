package com.todoapp.mobile.ui.pomodoro

import com.todoapp.mobile.R

object PomodoroContract {

    data class UiState(
        val min: Int = 25,
        val second: Int = 0,
        val mode: PomodoroModeUi = PomodoroModeUiPreset.Focus.value,
        val isRunning: Boolean = false,
        val isOvertime: Boolean = false,
        val infoMessage: String? = null,
    )

    sealed interface UiAction {
        data object SkipSession : UiAction
        data object StartCountDown : UiAction
        data object StopCountDown : UiAction
        data object ToggleBannerVisibility : UiAction
        data object Back : UiAction
    }

    sealed interface UiEffect {
        data object SessionFinished : UiEffect
    }
}

data class PomodoroModeUi(
    val title: String,
    val iconRes: Int,
    val colorKey: ModeColorKey
)
sealed interface PomodoroModeUiPreset {
    val value: PomodoroModeUi

    data object Focus : PomodoroModeUiPreset {
        override val value = PomodoroModeUi(
            title = "Focus",
            iconRes = R.drawable.ic_focus,
            colorKey = ModeColorKey.Focus,
        )
    }

    data object ShortBreak : PomodoroModeUiPreset {
        override val value = PomodoroModeUi(
            title = "Short Break",
            iconRes = R.drawable.ic_short_break,
            colorKey = ModeColorKey.ShortBreak,
        )
    }

    data object LongBreak : PomodoroModeUiPreset {
        override val value = PomodoroModeUi(
            title = "Long Break",
            iconRes = R.drawable.ic_long_break,
            colorKey = ModeColorKey.LongBreak,
        )
    }

    data object OverTime : PomodoroModeUiPreset {
        override val value = PomodoroModeUi(
            title = "Overtime",
            iconRes = R.drawable.ic_overtime,
            colorKey = ModeColorKey.OverTime,
        )
    }
}

enum class ModeColorKey {
    Focus,
    ShortBreak,
    LongBreak,
    OverTime,
}
