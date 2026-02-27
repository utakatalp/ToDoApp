package com.todoapp.mobile.navigation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.todoapp.mobile.LocalNavController
import com.todoapp.mobile.ui.activity.ActivityScreen
import com.todoapp.mobile.ui.activity.ActivityViewModel
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerScreen
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerViewModel
import com.todoapp.mobile.ui.banner.BannerOverlay
import com.todoapp.mobile.ui.banner.BannerViewModel
import com.todoapp.mobile.ui.calendar.CalendarScreen
import com.todoapp.mobile.ui.calendar.CalendarViewModel
import com.todoapp.mobile.ui.createnewgroup.CreateNewGroupScreen
import com.todoapp.mobile.ui.createnewgroup.CreateNewGroupViewModel
import com.todoapp.mobile.ui.details.DetailsScreen
import com.todoapp.mobile.ui.details.DetailsViewModel
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordScreen
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordViewModel
import com.todoapp.mobile.ui.groups.GroupScreen
import com.todoapp.mobile.ui.groups.GroupsViewModel
import com.todoapp.mobile.ui.home.HomeScreen
import com.todoapp.mobile.ui.home.HomeViewModel
import com.todoapp.mobile.ui.login.LoginScreen
import com.todoapp.mobile.ui.login.LoginViewModel
import com.todoapp.mobile.ui.login.findActivity
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.mobile.ui.pomodoro.PomodoroScreen
import com.todoapp.mobile.ui.pomodoro.PomodoroViewModel
import com.todoapp.mobile.ui.pomoodorofinish.PomodoroFinishScreen
import com.todoapp.mobile.ui.pomoodorofinish.PomodoroFinishViewModel
import com.todoapp.mobile.ui.register.RegisterScreen
import com.todoapp.mobile.ui.register.RegisterViewModel
import com.todoapp.mobile.ui.settings.SecretModeSettingsScreen
import com.todoapp.mobile.ui.settings.SettingsScreen
import com.todoapp.mobile.ui.settings.SettingsViewModel
import com.todoapp.mobile.ui.topbar.ShowTopBar
import com.todoapp.mobile.ui.topbar.TopBarViewModel
import com.todoapp.mobile.ui.webview.WebViewScreen
import com.todoapp.mobile.ui.webview.WebViewViewModel
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
            NavigationEffectController(navEffect)
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
            NavigationEffectController(navEffect)
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
                onAction = viewModel::onAction,
            )
        }

        composable<Screen.Settings> {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            NavigationEffectController(viewModel.navEffect)
            SettingsScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                onCheckPermissions = { viewModel.checkPermission(context) },
                onDismissPermission = viewModel::dismissPermission,
            )
        }

        composable<Screen.SecretMode> {
            val viewModel: SettingsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            SecretModeSettingsScreen(
                uiState,
                viewModel::onAction,
            )
        }

        composable<Screen.AddPomodoroTimer> {
            val viewModel: AddPomodoroTimerViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            AddPomodoroTimerScreen(
                uiState,
                viewModel::onAction
            )
        }
        composable<Screen.Pomodoro> {
            val viewModel: PomodoroViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            PomodoroScreen(
                uiState,
                uiEffect,
                viewModel::onAction
            )
        }

        composable<Screen.Task> {
            val viewModel: DetailsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            DetailsScreen(
                uiState,
                uiEffect,
                viewModel::onAction
            )
        }
        composable<Screen.Register> {
            val viewModel: RegisterViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            RegisterScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = uiEffect
            )
        }

        composable<Screen.Login> {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            LoginScreen(
                uiState = uiState,
                onAction = viewModel::onAction
            )
        }

        composable<Screen.WebView> {
            val viewModel: WebViewViewModel = hiltViewModel()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            WebViewScreen(
                onAction = viewModel::onAction,
                uiEffect = uiEffect
            )
        }

        composable<Screen.ForgotPassword> {
            val viewModel: ForgotPasswordViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            ForgotPasswordScreen(uiState, viewModel::onAction)
        }

        composable<Screen.PomodoroFinish> {
            val viewModel: PomodoroFinishViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            PomodoroFinishScreen(viewModel::onAction)
        }

        composable<Screen.CreateNewGroup> {
            val viewModel: CreateNewGroupViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            CreateNewGroupScreen(uiState, viewModel::onAction)
        }

        composable<Screen.Groups> {
            val viewModel: GroupsViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            GroupScreen(uiState, viewModel::onAction)
        }
        composable<Screen.Search> { }
        composable<Screen.Profile> { }
    }
}

@Composable
fun ToDoApp() {
    val bannerViewModel: BannerViewModel = hiltViewModel()
    val bannerState by bannerViewModel.uiState.collectAsStateWithLifecycle()
    val topBarViewModel: TopBarViewModel = hiltViewModel()
    val topBarState by topBarViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TDTheme.colors.background),
        bottomBar = { TDBottomBar() },
        topBar = {
            Column {
                BannerOverlay(
                    bannerState,
                    bannerViewModel::onAction,
                    bannerViewModel.uiEffect
                )
                NavigationEffectController(bannerViewModel.navEffect)
                ShowTopBar(bannerState.isBannerActivated, topBarViewModel::onAction, topBarState)
                NavigationEffectController(topBarViewModel.navEffect)
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->

        NavGraph(
            navController = LocalNavController.current,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

sealed interface NavigationEffect {
    data class Navigate(
        val route: Screen,
        val popUpTo: Screen? = null,
        val isInclusive: Boolean = false,
    ) : NavigationEffect

    data class NavigateClearingBackstack(val route: Screen) : NavigationEffect
    data object Back : NavigationEffect
    data object SystemBack : NavigationEffect
}

@Composable
fun NavigationEffectController(
    navEffect: Flow<NavigationEffect>,
) {
    val navController = LocalNavController.current
    navEffect.collectWithLifecycle { effect ->
        when (effect) {
            is NavigationEffect.Navigate -> {
                navController.navigate(effect.route) {
                    effect.popUpTo?.let {
                        popUpTo(it) {
                            inclusive = effect.isInclusive
                        }
                    }
                }
            }

            is NavigationEffect.Back -> {
                navController.popBackStack()
            }

            is NavigationEffect.NavigateClearingBackstack -> {
                navController.navigate(effect.route) {
                    popUpTo(0)
                }
            }

            NavigationEffect.SystemBack -> {
                val activity = navController.context.findActivity() as? ComponentActivity
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
}
