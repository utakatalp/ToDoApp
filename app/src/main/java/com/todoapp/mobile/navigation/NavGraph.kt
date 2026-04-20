package com.todoapp.mobile.navigation

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.todoapp.mobile.LocalNavController
import com.todoapp.mobile.MainActivity
import com.todoapp.mobile.MainViewModel
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
import com.todoapp.mobile.ui.filteredtasks.FilteredTasksScreen
import com.todoapp.mobile.ui.filteredtasks.FilteredTasksViewModel
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordScreen
import com.todoapp.mobile.ui.forgotpassword.ForgotPasswordViewModel
import com.todoapp.mobile.ui.groupdetail.GroupDetailScreen
import com.todoapp.mobile.ui.groupdetail.GroupDetailViewModel
import com.todoapp.mobile.ui.groups.GroupScreen
import com.todoapp.mobile.ui.groups.GroupsViewModel
import com.todoapp.mobile.ui.groupsettings.GroupSettingsScreen
import com.todoapp.mobile.ui.groupsettings.GroupSettingsViewModel
import com.todoapp.mobile.ui.grouptaskdetail.GroupTaskDetailScreen
import com.todoapp.mobile.ui.home.HomeScreen
import com.todoapp.mobile.ui.home.HomeViewModel
import com.todoapp.mobile.ui.invitemember.InviteMemberScreen
import com.todoapp.mobile.ui.invitemember.InviteMemberViewModel
import com.todoapp.mobile.ui.login.LoginScreen
import com.todoapp.mobile.ui.login.LoginViewModel
import com.todoapp.mobile.ui.managemembers.ManageMembersScreen
import com.todoapp.mobile.ui.managemembers.ManageMembersViewModel
import com.todoapp.mobile.ui.memberprofile.MemberProfileScreen
import com.todoapp.mobile.ui.memberprofile.MemberProfileViewModel
import com.todoapp.mobile.ui.onboarding.OnboardingScreen
import com.todoapp.mobile.ui.onboarding.OnboardingViewModel
import com.todoapp.mobile.ui.planyourday.PlanYourDayScreen
import com.todoapp.mobile.ui.planyourday.PlanYourDayViewModel
import com.todoapp.mobile.ui.pomodoro.PomodoroScreen
import com.todoapp.mobile.ui.pomodoro.PomodoroViewModel
import com.todoapp.mobile.ui.pomodorolaunch.PomodoroLaunchScreen
import com.todoapp.mobile.ui.pomodorolaunch.PomodoroLaunchViewModel
import com.todoapp.mobile.ui.pomodorosummary.PomodoroSummaryScreen
import com.todoapp.mobile.ui.pomodorosummary.PomodoroSummaryViewModel
import com.todoapp.mobile.ui.register.RegisterScreen
import com.todoapp.mobile.ui.register.RegisterViewModel
import com.todoapp.mobile.ui.search.SearchScreen
import com.todoapp.mobile.ui.search.SearchViewModel
import com.todoapp.mobile.ui.settings.SecretModeSettingsScreen
import com.todoapp.mobile.ui.settings.SettingsContract
import com.todoapp.mobile.ui.settings.SettingsScreen
import com.todoapp.mobile.ui.settings.SettingsViewModel
import com.todoapp.mobile.ui.splash.TDSplashScreen
import com.todoapp.mobile.ui.topbar.ShowTopBar
import com.todoapp.mobile.ui.topbar.TopBarViewModel
import com.todoapp.mobile.ui.webview.WebViewScreen
import com.todoapp.mobile.ui.webview.WebViewViewModel
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Screen,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 6 } },
        exitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { -it / 6 } },
        popEnterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { -it / 6 } },
        popExitTransition = { fadeOut(tween(180)) + slideOutHorizontally(tween(180)) { it / 6 } },
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
            NavigationEffectController(viewModel.navEffect)
            CalendarScreen(
                uiState = uiState,
                uiEffect = viewModel.effect,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Activity> {
            val viewModel: ActivityViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
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
            viewModel.uiEffect.collectWithLifecycle { effect ->
                when (effect) {
                    is SettingsContract.UiEffect.ShowToast -> Unit
                    SettingsContract.UiEffect.RecreateActivity -> {
                        val activity = context as? Activity ?: return@collectWithLifecycle
                        if (activity is MainActivity) MainActivity.suppressNextTransition.set(true)
                        activity.recreate()
                    }
                    is SettingsContract.UiEffect.ApplyLocale -> {
                        val locales = androidx.core.os.LocaleListCompat.forLanguageTags(effect.tag)
                        if (Build.VERSION.SDK_INT >= 33) {
                            // Android 13+: platform LocaleManager applies process-wide without full recreate.
                            val lm = context.getSystemService(android.app.LocaleManager::class.java)
                            lm?.applicationLocales = android.os.LocaleList.forLanguageTags(effect.tag)
                            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(locales)
                        } else {
                            // Pre-33: AppCompat routes through the delegate and auto-recreates.
                            androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(locales)
                        }
                    }
                }
            }
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
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
            )
        }

        composable<Screen.PlanYourDay> {
            val viewModel: PlanYourDayViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            PlanYourDayScreen(
                uiState = uiState,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable<Screen.PomodoroLaunch> {
            val viewModel: PomodoroLaunchViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            PomodoroLaunchScreen(uiState = uiState, onAction = viewModel::onAction)
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
                uiEffect = uiEffect,
            )
        }

        composable<Screen.Login> {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val uiEffect = viewModel.uiEffect
            NavigationEffectController(viewModel.navEffect)
            LoginScreen(
                uiState = uiState,
                onAction = viewModel::onAction,
                uiEffect = uiEffect,
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

        composable<Screen.PomodoroSummary> {
            val viewModel: PomodoroSummaryViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            PomodoroSummaryScreen(uiState = uiState, onAction = viewModel::onAction)
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
        composable<Screen.FilteredTasks> {
            val viewModel: FilteredTasksViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            FilteredTasksScreen(
                uiState = uiState,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
            )
        }

        composable<Screen.Search>(
            enterTransition = { slideInVertically { -it } + fadeIn() },
            exitTransition = { slideOutVertically { -it } + fadeOut() },
            popEnterTransition = { slideInVertically { -it } + fadeIn() },
            popExitTransition = { slideOutVertically { -it } + fadeOut() },
        ) {
            val viewModel: SearchViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NavigationEffectController(viewModel.navEffect)
            SearchScreen(
                uiState = uiState,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
            )
        }
        composable<Screen.Profile> { }

        composable<Screen.GroupDetail> {
            val viewModel: GroupDetailViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            GroupDetailScreen(viewModel = viewModel)
        }

        composable<Screen.GroupSettings> {
            val viewModel: GroupSettingsViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            GroupSettingsScreen(viewModel = viewModel)
        }

        composable<Screen.InviteMember> {
            val viewModel: InviteMemberViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            InviteMemberScreen(viewModel = viewModel)
        }

        composable<Screen.ManageMembers> {
            val viewModel: ManageMembersViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            ManageMembersScreen(viewModel = viewModel)
        }

        composable<Screen.MemberProfile> {
            val viewModel: MemberProfileViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            MemberProfileScreen(viewModel = viewModel)
        }

        composable<Screen.GroupTaskDetail> {
            GroupTaskDetailScreen()
        }

        composable<Screen.TransferOwnership> {
            val viewModel: com.todoapp.mobile.ui.transferownership.TransferOwnershipViewModel = hiltViewModel()
            NavigationEffectController(viewModel.navEffect)
            com.todoapp.mobile.ui.transferownership.TransferOwnershipScreen(viewModel = viewModel)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ToDoApp() {
    val bannerViewModel: BannerViewModel = hiltViewModel()
    val bannerState by bannerViewModel.uiState.collectAsStateWithLifecycle()
    val topBarViewModel: TopBarViewModel = hiltViewModel()
    val topBarState by topBarViewModel.uiState.collectAsStateWithLifecycle()
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn = mainViewModel.isLoggedIn

    if (isLoggedIn == null) {
        TDSplashScreen()
        return
    }

    val startDestination = remember { if (isLoggedIn) Screen.Home else Screen.Onboarding }
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TDTheme.colors.background),
        bottomBar = { if (isPortrait) TDBottomBar() },
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
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (!isPortrait) TDNavigationRail()
            NavGraph(
                navController = LocalNavController.current,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                startDestination = startDestination
            )
        }
    }
}

sealed interface NavigationEffect {
    data class Navigate(
        val route: Screen,
        val popUpTo: Screen? = null,
        val isInclusive: Boolean = false,
        val launchSingleTop: Boolean = false,
        val saveState: Boolean = false,
        val restoreState: Boolean = false,
    ) : NavigationEffect

    data class NavigateClearingBackstack(val route: Screen) : NavigationEffect
    data object Back : NavigationEffect
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
                            saveState = effect.saveState
                        }
                    }
                    launchSingleTop = effect.launchSingleTop
                    restoreState = effect.restoreState
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
        }
    }
}
