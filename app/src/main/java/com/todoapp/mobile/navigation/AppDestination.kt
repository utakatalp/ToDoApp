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
        selectedIcon = null,
    )

    data object PomodoroAddTimer : AppDestination(
        title = R.string.add_timer,
        route = Screen.AddPomodoroTimer::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object Task : AppDestination(
        title = R.string.task_details,
        route = Screen.Task::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object SecretMode : AppDestination(
        title = R.string.secret_mode_settings,
        route = Screen.SecretMode::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object PlanYourDay : AppDestination(
        title = R.string.plan_your_day,
        route = Screen.PlanYourDay::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object Groups : AppDestination(
        title = R.string.groups,
        route = Screen.Groups::class.qualifiedName!!,
        icon = R.drawable.ic_groups,
        selectedIcon = R.drawable.ic_selected_groups,
    )

    data object CreateNewGroup : AppDestination(
        title = R.string.new_group,
        route = Screen.CreateNewGroup::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object FilteredTasks : AppDestination(
        title = R.string.filtered_tasks_title,
        route = Screen.FilteredTasks::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object Search : AppDestination(
        title = R.string.search,
        route = Screen.Search::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object PomodoroLaunch : AppDestination(
        title = R.string.add_timer,
        route = Screen.PomodoroLaunch::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object PomodoroSummary : AppDestination(
        title = R.string.pomodoro_summary,
        route = Screen.PomodoroSummary::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object GroupDetail : AppDestination(
        title = R.string.group_detail,
        route = Screen.GroupDetail::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object GroupSettings : AppDestination(
        title = R.string.group_settings,
        route = Screen.GroupSettings::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object InviteMember : AppDestination(
        title = R.string.invite_member,
        route = Screen.InviteMember::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object ManageMembers : AppDestination(
        title = R.string.manage_members,
        route = Screen.ManageMembers::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object GroupTaskDetail : AppDestination(
        title = R.string.group_task_detail,
        route = Screen.GroupTaskDetail::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object MemberProfile : AppDestination(
        title = R.string.member_profile,
        route = Screen.MemberProfile::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object TransferOwnership : AppDestination(
        title = R.string.transfer_ownership,
        route = Screen.TransferOwnership::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    data object Profile : AppDestination(
        title = R.string.profile,
        route = Screen.Profile::class.qualifiedName!!,
        icon = null,
        selectedIcon = null,
    )

    companion object {
        val bottomBarItems = listOf(Home, Groups, Calendar, Activity)
        val topBarItems =
            listOf(
                Home,
                Calendar,
                Activity,
                PomodoroAddTimer,
                Settings,
                Task,
                SecretMode,
                PlanYourDay,
                Groups,
                CreateNewGroup,
                FilteredTasks,
                Search,
                PomodoroLaunch,
                PomodoroSummary,
                GroupDetail,
                GroupSettings,
                InviteMember,
                ManageMembers,
                GroupTaskDetail,
                MemberProfile,
                TransferOwnership,
                Profile,
            )
    }
}

fun bottomBarAppDestinationFromRoute(route: String?) = AppDestination.bottomBarItems.firstOrNull { it.route == route }

fun appDestinationFromRoute(route: String?) = AppDestination.topBarItems.firstOrNull { it.route == route }
