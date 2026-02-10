package com.todoapp.mobile.navigation

import kotlinx.serialization.Serializable

interface Screen {
    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object AddPomodoroTimer : Screen

    @Serializable
    data object Pomodoro : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object SecretMode : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Activity : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Task : Screen

    @Serializable
    data class Edit(val taskId: Long) : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data class WebView(val url: String) : Screen

    @Serializable
    data object ForgotPassword : Screen

    @Serializable
    data object PomodoroFinish : Screen
}
