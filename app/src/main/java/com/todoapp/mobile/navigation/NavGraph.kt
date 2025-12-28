package com.todoapp.mobile.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.uikit.R
import com.todoapp.mobile.common.CollectWithLifecycle
import com.todoapp.mobile.ui.home.HomeScreen
import com.todoapp.mobile.ui.home.HomeViewModel
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiEffect
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.uikit.components.TDTopBar
import com.todoapp.uikit.components.TopBarAction
import com.todoapp.uikit.components.TopBarState
import kotlinx.coroutines.flow.Flow

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen = Screen.Onboarding,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Screen.Onboarding> {
            val viewModel: OnboardingViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(uiEffect, navController)
            OnboardingScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Home> {
            val viewModel: HomeViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                modifier =
                    Modifier
                        .padding(start = 24.dp, end = 24.dp),
            )
        }
        composable<Screen.Settings> { }
        composable<Screen.Notifications> { }
        composable<Screen.Search> { }
        composable<Screen.Calendar> { }
        composable<Screen.Statistic> { }
        composable<Screen.Profile> { }
    }
}

@Composable
private fun NavigationEffectController(
    uiEffect: Flow<UiEffect>,
    navController: NavHostController,
) {
    uiEffect.CollectWithLifecycle { effect ->
        when (effect) {
            UiEffect.NavigateToLogin -> {
                navController.navigate(Screen.Home)
            }

            UiEffect.NavigateToRegister -> {
                navController.navigate(Screen.Home)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    val isOnboarding = destination?.hasRoute<Screen.Onboarding>() == true
    val showTopBar = !isOnboarding
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ShowTopBar(showTopBar, navController) },
        bottomBar = { TDBottomBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        NavGraph(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding),
            navController = navController,
        )
    }
}

@Composable
private fun ShowTopBar(
    showTopBar: Boolean,
    navController: NavHostController,
) {
    if (showTopBar) {
        TDTopBar(
            state =
                TopBarState(
                    title = "Home",
                    navigationIcon = R.drawable.ic_settings,
                    onNavigationClick = { navController.navigate(Screen.Settings) },
                    actions =
                        listOf(
                            TopBarAction(
                                icon = R.drawable.ic_search,
                                onClick = { navController.navigate(Screen.Search) },
                            ),
                            TopBarAction(
                                icon = R.drawable.ic_notification,
                                onClick = { navController.navigate(Screen.Notifications) },
                            ),
                        ),
                ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {},
        actions = {
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = "settings",
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "settings",
                )
            }
            IconButton(
                onClick = {},
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = "settings",
                )
            }
        },
    )
}
