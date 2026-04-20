package com.todoapp.mobile.ui.pomodoro

import androidx.compose.ui.graphics.Color

data class PomodoroModePalette(
    val background: Color,
    val surface: Color,
    val content: Color,
    val track: Color,
    val lightShadow: Color,
    val darkShadow: Color,
)

object PomodoroModeTheme {

    private val focusLight = PomodoroModePalette(
        background = Color(0xFFF0FFF4),
        surface = Color(0xFFD4EDDA),
        content = Color(0xFF1A4731),
        track = Color(0xFFB7DFCA),
        lightShadow = Color(0xFFFFFFFF).copy(alpha = 0.85f),
        darkShadow = Color(0xFF1A4731).copy(alpha = 0.18f),
    )
    private val focusDark = PomodoroModePalette(
        background = Color(0xFF1A2E23),
        surface = Color(0xFF2D5A3D),
        content = Color(0xFF48BB78),
        track = Color(0xFF234835),
        lightShadow = Color(0xFF3A7A52).copy(alpha = 0.5f),
        darkShadow = Color(0xFF000000).copy(alpha = 0.45f),
    )

    private val shortBreakLight = PomodoroModePalette(
        background = Color(0xFFFFF5F0),
        surface = Color(0xFFFFDDD0),
        content = Color(0xFF7B2D20),
        track = Color(0xFFFAC4B0),
        lightShadow = Color(0xFFFFFFFF).copy(alpha = 0.85f),
        darkShadow = Color(0xFF7B2D20).copy(alpha = 0.18f),
    )
    private val shortBreakDark = PomodoroModePalette(
        background = Color(0xFF2D1A15),
        surface = Color(0xFF5C2A20),
        content = Color(0xFFFC8181),
        track = Color(0xFF3D201A),
        lightShadow = Color(0xFF7A3A30).copy(alpha = 0.5f),
        darkShadow = Color(0xFF000000).copy(alpha = 0.45f),
    )

    private val longBreakLight = PomodoroModePalette(
        background = Color(0xFFEBF8FF),
        surface = Color(0xFFBEE3F8),
        content = Color(0xFF1A365D),
        track = Color(0xFFA0CDE8),
        lightShadow = Color(0xFFFFFFFF).copy(alpha = 0.85f),
        darkShadow = Color(0xFF1A365D).copy(alpha = 0.18f),
    )
    private val longBreakDark = PomodoroModePalette(
        background = Color(0xFF0D1B2A),
        surface = Color(0xFF1A3A5C),
        content = Color(0xFF63B3ED),
        track = Color(0xFF152840),
        lightShadow = Color(0xFF1F4A72).copy(alpha = 0.5f),
        darkShadow = Color(0xFF000000).copy(alpha = 0.45f),
    )

    private val overtimeLight = PomodoroModePalette(
        background = Color(0xFFFFF5F5),
        surface = Color(0xFFFED7D7),
        content = Color(0xFF742A2A),
        track = Color(0xFFF9B8B8),
        lightShadow = Color(0xFFFFFFFF).copy(alpha = 0.85f),
        darkShadow = Color(0xFF742A2A).copy(alpha = 0.18f),
    )
    private val overtimeDark = PomodoroModePalette(
        background = Color(0xFF2D1515),
        surface = Color(0xFF5C2020),
        content = Color(0xFFF56565),
        track = Color(0xFF3D1C1C),
        lightShadow = Color(0xFF7A2828).copy(alpha = 0.5f),
        darkShadow = Color(0xFF000000).copy(alpha = 0.45f),
    )

    fun resolve(colorKey: ModeColorKey, isDark: Boolean): PomodoroModePalette = when (colorKey) {
        ModeColorKey.Focus -> if (isDark) focusDark else focusLight
        ModeColorKey.ShortBreak -> if (isDark) shortBreakDark else shortBreakLight
        ModeColorKey.LongBreak -> if (isDark) longBreakDark else longBreakLight
        ModeColorKey.OverTime -> if (isDark) overtimeDark else overtimeLight
    }
}
