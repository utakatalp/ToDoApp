package com.todoapp.mobile.ui.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.todoapp.mobile.R
import com.todoapp.uikit.theme.TDTheme

object PomodoroContract {

    data class UiState(
        val min: Int = 25,
        val second: Int = 0,
        val mode: PomodoroMode = PomodoroMode.Focus,
        val isStopWatchRunning: Boolean = false,
        val isOvertime: Boolean = false,
        val infoMessage: String? = null,
    )

    sealed interface UiAction {
        data object SkipSession : UiAction
        data object StartCountDown : UiAction
        data object StopCountDown : UiAction
    }

    sealed interface UiEffect {
        data class SessionFinished(val mode: PomodoroMode) : UiEffect
    }
}
sealed class PomodoroMode(
    val title: String,
    val iconRes: Int,
    val colorKey: ModeColorKey,
) {
    object Focus : PomodoroMode(
        title = "Focus",
        iconRes = R.drawable.ic_focus,
        colorKey = ModeColorKey.Focus
    )

    object ShortBreak : PomodoroMode(
        title = "Short Break",
        iconRes = R.drawable.ic_short_break,
        colorKey = ModeColorKey.ShortBreak
    )

    object LongBreak : PomodoroMode(
        title = "Long Break",
        iconRes = R.drawable.ic_long_break,
        colorKey = ModeColorKey.LongBreak
    )

    object OverTime : PomodoroMode(
        title = "Overtime",
        iconRes = R.drawable.ic_overtime,
        colorKey = ModeColorKey.OverTime
    )
}

data class Session(
    val duration: Long,
    val mode: PomodoroMode,
)
enum class ModeColorKey {
    Focus,
    ShortBreak,
    LongBreak,
    OverTime
}

@Composable
fun PomodoroMode.resolveTextColor(): Color {
    return when (colorKey) {
        ModeColorKey.Focus -> TDTheme.colors.primary
        ModeColorKey.ShortBreak -> TDTheme.colors.softPink
        ModeColorKey.LongBreak -> TDTheme.colors.green
        ModeColorKey.OverTime -> TDTheme.colors.red
    }
}
