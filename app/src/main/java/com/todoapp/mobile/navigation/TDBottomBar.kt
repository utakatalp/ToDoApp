package com.todoapp.mobile.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.todoapp.mobile.LocalNavController
import com.todoapp.uikit.theme.TDTheme

fun shouldShowNav(currentDestination: NavDestination?): Boolean {
    val topLevelRoutes = AppDestination.bottomBarItems.map { it.route }.toSet()
    return currentDestination?.hierarchy?.any { dest ->
        dest.route?.substringBefore("?")?.substringBefore("/") in topLevelRoutes
    } == true
}

@Composable
fun TDBottomBar() {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    if (!shouldShowNav(currentDestination)) return

    NavigationBar(
        containerColor = TDTheme.colors.background,
    ) {
        AppDestination.bottomBarItems.forEach { screen ->
            val screenRoute = screen.route
            val selected =
                currentDestination?.hierarchy?.any { dest ->
                    dest.route?.substringBefore("?")?.substringBefore("/") == screenRoute
                } == true

            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TDTheme.colors.pendingGray,
                    selectedTextColor = TDTheme.colors.pendingGray,
                    indicatorColor = TDTheme.colors.pendingGray.copy(alpha = 0.2f)
                ),
                onClick = {
                    if (selected) return@NavigationBarItem
                    navController.navigate(screenRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val iconId = if (selected) screen.selectedIcon else screen.icon
                    Icon(
                        painter = painterResource(id = iconId!!),
                        contentDescription = stringResource(screen.title),
                        tint = Color.Unspecified,
                    )
                },
                label = { Text(text = stringResource(id = screen.title)) },
                alwaysShowLabel = false,
            )
        }
    }
}
