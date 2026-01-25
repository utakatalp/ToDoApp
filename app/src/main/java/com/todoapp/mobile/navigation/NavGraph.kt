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
import com.todoapp.mobile.common.CollectWithLifecycle
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
            NavigationEffectHandler(
                effect = uiEffect,
                navController = navController,
            ) { effect, controller ->
                when (effect) {
                    is OnboardingContract.UiEffect.NavigateToLogin -> {
                        controller.navigate(Screen.Home)
                    }

                    is OnboardingContract.UiEffect.NavigateToRegister -> {
                        controller.navigate(Screen.Home)
                    }
                }
            }
            OnboardingScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Home> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val navigationEffect = viewModel.navigationEffect
            NavigationEffectHandler(
                effect = navigationEffect,
                navController = navController,
            ) { effect, controller ->
                when (effect) {
                    is HomeContract.NavigationEffect.NavigateToEdit -> {
                        controller.navigate(Screen.Edit(taskId = effect.taskId))
                    }
                }
            }
            HomeScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Calendar> {
            val viewModel: CalendarViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CalendarScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }

        composable<Screen.Edit> {
            val viewModel: EditViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val navigationEffect = viewModel.navigationEffect
            val context = LocalContext.current

            NavigationEffectHandler(
                effect = navigationEffect,
                navController = navController,
            ) { effect, controller ->
                when (effect) {
                    EditContract.NavigationEffect.NavigateBack -> {
                        controller.popBackStack()
                    }
                }
            }

            viewModel.uiEffect.CollectWithLifecycle { effect ->
                when (effect) {
                    is EditContract.UiEffect.ShowToast -> {
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            EditScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
            )
        }

        composable<Screen.Settings> {}
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

@Composable
private fun <T> NavigationEffectHandler(
    effect: Flow<T>,
    navController: NavHostController,
    onEffect: (T, NavController) -> Unit,
) {
    effect.CollectWithLifecycle { navEffect ->
        onEffect(navEffect, navController)
    }
}
