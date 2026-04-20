package com.todoapp.mobile.ui.banner

import androidx.annotation.DrawableRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.todoapp.mobile.R
import com.todoapp.mobile.common.RingtoneHolder
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.todoapp.mobile.ui.banner.BannerContract.UiAction
import com.todoapp.mobile.ui.banner.BannerContract.UiState
import com.todoapp.mobile.ui.pomodoro.ModeColorKey
import com.todoapp.mobile.ui.pomodoro.PomodoroModeTheme
import com.todoapp.uikit.components.TDPomodoroBanner
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun BannerOverlay(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    uiEffect: Flow<BannerContract.UiEffect>,
) {
    val ringToneHolder = remember { RingtoneHolder() }
    val context = LocalContext.current

    LaunchedEffect(uiEffect) {
        uiEffect.collect { effect ->
            when (effect) {
                is BannerContract.UiEffect.SessionFinished -> {
                    ringToneHolder.play(context)
                }
            }
        }
    }

    BannerContent(uiState, onAction)
}

@Composable
fun BannerContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    if (!uiState.isVisible) return

    val isDark = isSystemInDarkTheme()
    val palette = PomodoroModeTheme.resolve(uiState.mode.toModeColorKey(), isDark)

    TDPomodoroBanner(
        minutes = uiState.minutes ?: 0,
        seconds = uiState.seconds ?: 0,
        isBannerActivated = uiState.isBannerActivated,
        isOverTime = uiState.isOverTime ?: false,
        modeLabel = uiState.mode.toLabel(),
        modeIconRes = uiState.mode.toIconRes(),
        backgroundColor = palette.surface,
        contentColor = palette.content,
        onClick = { onAction(UiAction.OnBannerTap) },
    )
}

private fun PomodoroMode.toModeColorKey(): ModeColorKey = when (this) {
    PomodoroMode.Focus -> ModeColorKey.Focus
    PomodoroMode.ShortBreak -> ModeColorKey.ShortBreak
    PomodoroMode.LongBreak -> ModeColorKey.LongBreak
    PomodoroMode.OverTime -> ModeColorKey.OverTime
}

private fun PomodoroMode.toLabel(): String = when (this) {
    PomodoroMode.Focus -> "Focus"
    PomodoroMode.ShortBreak -> "Short Break"
    PomodoroMode.LongBreak -> "Long Break"
    PomodoroMode.OverTime -> "Overtime"
}

@DrawableRes
private fun PomodoroMode.toIconRes(): Int = when (this) {
    PomodoroMode.Focus -> R.drawable.ic_focus
    PomodoroMode.ShortBreak -> R.drawable.ic_short_break
    PomodoroMode.LongBreak -> R.drawable.ic_long_break
    PomodoroMode.OverTime -> R.drawable.ic_overtime
}

@Preview(showBackground = true)
@Composable
private fun BannerContentPreview() {
    TDTheme {
        BannerContent(
            uiState = UiState(
                isVisible = true,
                isBannerActivated = true,
                minutes = 25,
                seconds = 0,
                mode = PomodoroMode.Focus,
                isOverTime = false
            ),
            onAction = {}
        )
    }
}
