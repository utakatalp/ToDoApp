package com.todoapp.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.todoapp.mobile.ui.OnboardingScreen
import com.todoapp.mobile.ui.OnboardingViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: Screen,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Screen.Onboarding> {
            val viewModel: OnboardingViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            OnboardingScreen(
                uiState = uiState,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
                onNavigateToRegister = {},
                onNavigateToLogin = {}
            )
        }
    }
}
