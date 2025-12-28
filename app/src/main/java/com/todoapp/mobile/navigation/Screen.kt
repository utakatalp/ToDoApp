package com.todoapp.mobile.navigation

import kotlinx.serialization.Serializable

interface Screen {
    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Statistic : Screen

    @Serializable
    data object Profile : Screen
}
