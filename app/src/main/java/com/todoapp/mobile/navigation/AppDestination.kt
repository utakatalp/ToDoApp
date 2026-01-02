package com.todoapp.mobile.navigation

import com.todoapp.mobile.R

sealed class AppDestination(
    val title: Int,
    val route: String,
    val icon: Int,
    val selectedIcon: Int,
) {
    data object Home : AppDestination(
        title = R.string.navbar_home_screen_page_name,
        route = Screen.Home::class.qualifiedName!!,
        icon = R.drawable.ic_home,
        selectedIcon = R.drawable.ic_selected_home,
    )

    data object Calendar : AppDestination(
        title = R.string.navbar_calendar_screen_page_name,
        route = Screen.Calendar::class.qualifiedName!!,
        icon = R.drawable.ic_calendar,
        selectedIcon = R.drawable.ic_selected_calendar,
    )

    data object Activity : AppDestination(
        title = R.string.navbar_statistic_screen_page_name,
        route = Screen.Statistic::class.qualifiedName!!,
        icon = R.drawable.ic_statistic,
        selectedIcon = R.drawable.ic_selected_statistic,
    )

    companion object {
        val bottomBarItems = listOf(Home, Calendar, Activity)
    }
}

fun appDestinationFromRoute(route: String?) = AppDestination.bottomBarItems.firstOrNull { it.route == route }
