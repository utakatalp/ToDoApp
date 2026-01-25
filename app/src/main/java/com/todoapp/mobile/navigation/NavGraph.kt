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
import androidx.compose.ui.platform.LocalContext
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
import com.todoapp.mobile.ui.onboarding.OnboardingContract.UiEffect
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.uikit.components.TDOverlayPermissionItem
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
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(uiEffect, navController)
            OnboardingScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            HomeScreen(
                uiState = uiState,
                uiEffect = uiEffect,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Calendar> {
            val viewModel: CalendarViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CalendarScreen(
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
        
        composable<Screen.Settings> {
            TDOverlayPermissionItem(LocalContext.current)
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
