package com.todoapp.mobile.navigation

import com.todoapp.mobile.R
import com.todoapp.mobile.navigation.AppDestination.SecretMode

sealed class AppDestination(
    val title: Int,
    val route: String,
    val icon: Int?,
    val selectedIcon: Int?,
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
        route = Screen.Activity::class.qualifiedName!!,
        icon = R.drawable.ic_statistic,
        selectedIcon = R.drawable.ic_selected_statistic,
    )
    data object Settings : AppDestination(
        title = R.string.settings,
        route = Screen.Settings::class.qualifiedName!!,
        icon = null,
        selectedIcon = null
    )

    data object PomodoroAddTimer : AppDestination(
        title = R.string.add_timer,
        route = Screen.AddPomodoroTimer::class.qualifiedName!!,
        icon = null,
        selectedIcon = null
    )

    data object Task : AppDestination(
        title = R.string.task_details,
        route = Screen.Task::class.qualifiedName!!,
        icon = null,
        selectedIcon = null
    )

    data object SecretMode : AppDestination(
        title = R.string.secret_mode_settings,
        route = Screen.SecretMode::class.qualifiedName!!,
        icon = null,
        selectedIcon = null
    )

    companion object {
        val bottomBarItems = listOf(Home, Calendar, Activity)
        val items = listOf(Home, Calendar, Activity, PomodoroAddTimer, Settings, Task, SecretMode)
    }
}

fun bottomBarAppDestinationFromRoute(route: String?) = AppDestination.bottomBarItems.firstOrNull { it.route == route }
fun appDestinationFromRoute(route: String?) = AppDestination.items.firstOrNull { it.route == route }
