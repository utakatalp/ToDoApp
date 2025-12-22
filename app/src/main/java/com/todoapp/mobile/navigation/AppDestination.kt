package com.todoapp.mobile.navigation

import com.example.uikit.R

sealed class AppDestination(
    val title: String,
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
) {
    data object Home : AppDestination(
        title = "Home",
        route = Screen.Home::class.qualifiedName!!,
        icon = R.drawable.ic_home,
        selectedIcon = R.drawable.ic_selected_home,
    )

    data object Statistic : AppDestination(
        title = "Statistic",
        route = Screen.Statistic::class.qualifiedName!!,
        icon = R.drawable.ic_statistic,
        selectedIcon = R.drawable.ic_selected_statistic,
    )

    data object Calendar : AppDestination(
        title = "Calendar",
        route = Screen.Calendar::class.qualifiedName!!,
        icon = R.drawable.ic_calendar,
        selectedIcon = R.drawable.ic_selected_calendar,
    )

    companion object {
        val bottomBarItems = listOf(Home, Statistic, Calendar)
    }
}
