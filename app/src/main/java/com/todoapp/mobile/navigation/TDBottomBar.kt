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
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDBottomBar(
    navController: NavHostController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val items = AppDestination.bottomBarItems
    val topLevelRoutes = items.map { it.route }.toSet()
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        dest.route in topLevelRoutes
    } == true

    if (!showBottomBar) return

    NavigationBar(
        containerColor = TDTheme.colors.background
    ) {
        items.forEach { screen ->
            val screenRoute = screen.route
            val selected = currentDestination?.hierarchy?.any { dest ->
                dest.route == screenRoute
            } == true

            NavigationBarItem(
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TDTheme.colors.purple,
                    selectedTextColor = TDTheme.colors.purple,
                    indicatorColor = TDTheme.colors.purple.copy(alpha = 0.2f)
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
                        painter = painterResource(id = iconId),
                        contentDescription = stringResource(screen.title),
                        tint = Color.Unspecified
                    )
                },
                label = { Text(text = stringResource(id = screen.title)) },
                alwaysShowLabel = false
            )
        }
    }
}
