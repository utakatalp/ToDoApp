package com.todoapp.mobile.navigation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.todoapp.mobile.ui.activity.ActivityScreen
import com.todoapp.mobile.ui.activity.ActivityViewModel
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerScreen
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerViewModel
import com.todoapp.mobile.ui.calendar.CalendarScreen
import com.todoapp.mobile.ui.calendar.CalendarViewModel
import com.todoapp.mobile.ui.edit.EditContract
import com.todoapp.mobile.ui.edit.EditScreen
import com.todoapp.mobile.ui.edit.EditViewModel
import com.todoapp.mobile.ui.home.HomeContract
import com.todoapp.mobile.ui.home.HomeScreen
import com.todoapp.mobile.ui.home.HomeViewModel
import com.todoapp.mobile.ui.onboarding.OnboardingContract
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.mobile.ui.pomodoro.PomodoroScreen
import com.todoapp.mobile.ui.pomodoro.PomodoroViewModel
import com.todoapp.mobile.ui.settings.SettingsScreen
import com.todoapp.mobile.ui.settings.SettingsViewModel
import com.todoapp.uikit.extensions.collectWithLifecycle
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
            NavigationEffectController(navController, navEffect)
        }
        composable<Screen.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            val navEffect = viewModel.navEffect
            HomeScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
            NavigationEffectController(navController, navEffect)
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
        composable<Screen.AddPomodoroTimer> {
            val viewModel: AddPomodoroTimerViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(navController, viewModel.navEffect)
            AddPomodoroTimerScreen(
                uiState,
                viewModel::onAction
            )
        }
        composable<Screen.Pomodoro> {
            val viewModel: PomodoroViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(navController, viewModel.navEffect)
            PomodoroScreen(
                uiState,
                uiEffect,
                viewModel::onAction
            )
        }
        composable<Screen.Notifications> { }
        composable<Screen.Search> { }
        composable<Screen.Profile> { }
        composable<Screen.Task> { }
    }
}

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

sealed interface NavigationEffect {
    data class Navigate(val route: Screen) : NavigationEffect
    data object Back : NavigationEffect
}

@Composable
private fun NavigationEffectController(
    navController: NavHostController,
    navEffect: Flow<NavigationEffect>,
) {
    navEffect.collectWithLifecycle { effect ->
        when (effect) {
            is NavigationEffect.Navigate -> {
                navController.navigate(effect.route)
            }
            is NavigationEffect.Back -> {
                navController.popBackStack()
            }
        }
    }
}
