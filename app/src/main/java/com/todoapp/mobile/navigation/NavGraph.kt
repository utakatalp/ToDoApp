package com.todoapp.mobile.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.todoapp.mobile.common.CollectWithLifecycle
import com.todoapp.mobile.ui.activity.ActivityScreen
import com.todoapp.mobile.ui.activity.ActivityViewModel
import com.todoapp.mobile.ui.calendar.CalendarScreen
import com.todoapp.mobile.ui.calendar.CalendarViewModel
import com.todoapp.mobile.ui.home.HomeScreen
import com.todoapp.mobile.ui.home.HomeViewModel
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.mobile.ui.settings.SettingsScreen
import com.todoapp.mobile.ui.settings.SettingsViewModel
import com.todoapp.uikit.theme.TDTheme
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
            val navEffect = viewModel.navEffect
            OnboardingScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
            NavigationEffectController(navEffect, navController)
        }
        composable<Screen.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            val navEffect = viewModel.navEffect
            HomeScreen(
                uiState = uiState,
                uiEffect = uiEffect,
                onAction = viewModel::onAction,
            )
            NavigationEffectController(navEffect, navController)
        }
        composable<Screen.Calendar> {
            val viewModel: CalendarViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CalendarScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Settings> {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Activity> {
            val viewModel: ActivityViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ActivityScreen(
                uiState = uiState,
            )
        }
        composable<Screen.Notifications> { }
        composable<Screen.Search> { }
        composable<Screen.Profile> { }
        composable<Screen.Task> { }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ToDoApp() {
    val navController = rememberNavController()
    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TDTheme.colors.white),
        topBar = { ShowTopBar(navController) },
        bottomBar = { TDBottomBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun NavigationEffectController(
    navEffect: Flow<NavEffect>,
    navController: NavHostController,
) {
    navEffect.CollectWithLifecycle { effect ->
        when (effect) {
            NavEffect.NavigateToLogin -> navController.navigate(Screen.Home)
            NavEffect.NavigateToRegister -> navController.navigate(Screen.Home)
            NavEffect.NavigateToSettings -> navController.navigate(Screen.Settings)
        }
    }
}

sealed interface NavEffect {
    data object NavigateToLogin : NavEffect
    data object NavigateToRegister : NavEffect
    data object NavigateToSettings : NavEffect
}
