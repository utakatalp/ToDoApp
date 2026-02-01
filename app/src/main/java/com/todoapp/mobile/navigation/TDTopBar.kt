package com.todoapp.mobile.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDTopBar(
    state: TDTopBarState,
    isBannerActivated: Boolean
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = state.title,
                textAlign = TextAlign.Center,
                style = TDTheme.typography.heading4,
                color = TDTheme.colors.onBackground
            )
        },
        navigationIcon = {
            state.navigationIcon.let {
                IconButton(onClick = state.onNavigationClick!!) {
                    Icon(painterResource(it), tint = TDTheme.colors.onBackground, contentDescription = null)
                }
            }
        },
        actions = {
            state.actions.forEach {
                IconButton(onClick = it.onClick) {
                    Icon(painterResource(it.icon), tint = TDTheme.colors.onBackground, contentDescription = null)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TDTheme.colors.background),
        windowInsets = if (isBannerActivated) WindowInsets(0, 0, 0, 0) else TopAppBarDefaults.windowInsets
    )
}

@Composable
fun ShowTopBar(
    navController: NavHostController,
    isBannerActivated: Boolean
) {
    val route =
        navController
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route
    val destination = appDestinationFromRoute(route) ?: return
    val titleText = stringResource(destination.title)
    val state = when (destination) {
        AppDestination.Home ->
            TDTopBarState(
                title = titleText,
                onNavigationClick = { navController.navigate(Screen.Settings) },
                navigationIcon = R.drawable.ic_settings,
                actions =
                    listOf(
                        TDTopBarAction(
                            icon = R.drawable.ic_notification,
                            onClick = { navController.navigate(Screen.Notifications) },
                        ),
                    ),
            )
        else -> {
            TDTopBarState(
                title = titleText,
                onNavigationClick = { navController.popBackStack() },
                navigationIcon = R.drawable.ic_arrow_back,
            )
        }
    }

    TDTopBar(state = state, isBannerActivated)
}

data class TDTopBarState(
    val title: String,
    @DrawableRes val navigationIcon: Int,
    val onNavigationClick: (() -> Unit)? = null,
    val actions: List<TDTopBarAction> = emptyList(),
)

data class TDTopBarAction(
    @DrawableRes val icon: Int,
    val onClick: () -> Unit,
)

@Preview(showBackground = true, uiMode = AndroidUiModes.UI_MODE_NIGHT_YES, widthDp = 360)
@Composable
private fun TDTopBarPreview_Home() {
    TDTheme {
        TDTopBar(
            state =
                TDTopBarState(
                    title = "Home",
                    navigationIcon = R.drawable.ic_settings,
                    onNavigationClick = {},
                    actions =
                        listOf(
                            TDTopBarAction(
                                icon = R.drawable.ic_hamburger,
                                onClick = {},
                            ),
                            TDTopBarAction(
                                icon = R.drawable.ic_notification,
                                onClick = {},
                            ),
                        ),
                ),
            isBannerActivated = false,
        )
    }
}

@Preview(showBackground = true, uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, widthDp = 360)
@Composable
private fun TDTopBarPreview_Calendar() {
    TDTheme {
        TDTopBar(
            state =
                TDTopBarState(
                    title = "Calendar",
                    navigationIcon = R.drawable.ic_arrow_back,
                    onNavigationClick = { },
                    actions = listOf(
                        TDTopBarAction(
                            icon = R.drawable.ic_hamburger,
                            onClick = {},
                        ),
                    ),
                ),
            isBannerActivated = true,
        )
    }
}
