package com.todoapp.mobile.ui.settings

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.ThemePreference
import com.todoapp.mobile.domain.security.SecretModeReopenOptions
import com.todoapp.mobile.ui.settings.SettingsContract.UiAction
import com.todoapp.mobile.ui.settings.SettingsContract.UiState
import com.todoapp.uikit.components.TDNotificationPermissionItem
import com.todoapp.uikit.components.TDOverlayPermissionItem
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SettingsScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    onCheckPermissions: () -> Unit,
    onDismissPermission: (PermissionType) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            onCheckPermissions()
        }
    }

    SettingsContent(
        uiState = uiState,
        onAction = onAction,
        onDismissPermission = onDismissPermission,
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    onDismissPermission: (PermissionType) -> Unit,
) {
    val context = LocalContext.current

    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { onAction(UiAction.OnLogoutDismiss) },
            title = {
                TDText(
                    text = stringResource(R.string.logout_dialog_title),
                    style = TDTheme.typography.heading5,
                    color = TDTheme.colors.onBackground,
                )
            },
            text = {
                TDText(
                    text = stringResource(R.string.logout_dialog_message),
                    style = TDTheme.typography.subheading1,
                    color = TDTheme.colors.onBackground,
                )
            },
            confirmButton = {
                TextButton(onClick = { onAction(UiAction.OnLogoutConfirm) }) {
                    TDText(
                        text = stringResource(R.string.logout),
                        style = TDTheme.typography.subheading1,
                        color = TDTheme.colors.crossRed,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(UiAction.OnLogoutDismiss) }) {
                    TDText(
                        text = stringResource(R.string.cancel),
                        style = TDTheme.typography.subheading1,
                        color = TDTheme.colors.onBackground,
                    )
                }
            },
            containerColor = TDTheme.colors.surface,
        )
    }

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        PermissionPager(
            context = context,
            permissions = uiState.visiblePermissions,
            onDismiss = onDismissPermission,
        )

        Spacer(modifier = Modifier.height(12.dp))

        ThemeSelector(
            currentTheme = uiState.currentTheme,
            onThemeChange = { theme ->
                onAction(UiAction.OnThemeChange(theme))
            },
        )

        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.1f))

        LanguageSelector(
            currentLanguage = uiState.currentLanguage,
            onLanguageChange = { language ->
                onAction(UiAction.OnLanguageChange(language))
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onAction(UiAction.OnNavigateToPlanYourDay) },
        ) {
            TDText(
                text = stringResource(R.string.plan_your_day),
                style = TDTheme.typography.heading6,
                color = TDTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TDTheme.colors.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onAction(UiAction.OnNavigateToPomodoroSettings) },
        ) {
            TDText(
                text = stringResource(R.string.pomodoro_configure_timer),
                style = TDTheme.typography.heading6,
                color = TDTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TDTheme.colors.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onAction(UiAction.OnNavigateToSecretModeSettings) },
        ) {
            TDText(
                text = stringResource(R.string.privacy_security),
                style = TDTheme.typography.heading6,
                color = TDTheme.colors.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TDTheme.colors.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.1f))
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isUserAuthenticated) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onAction(UiAction.OnLogoutClick) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TDText(
                    text = stringResource(R.string.logout),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.crossRed,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = null,
                    tint = TDTheme.colors.crossRed,
                )
            }
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onAction(UiAction.OnLoginOrRegisterClick) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TDText(
                    text = stringResource(R.string.login_or_create_account),
                    style = TDTheme.typography.heading6,
                    color = TDTheme.colors.darkPending,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                    contentDescription = null,
                    tint = TDTheme.colors.darkPending,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun PermissionPager(
    context: Context,
    permissions: List<PermissionType>,
    onDismiss: (PermissionType) -> Unit,
) {
    if (permissions.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { permissions.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { index ->
            when (permissions[index]) {
                PermissionType.NOTIFICATION ->
                    TDNotificationPermissionItem(
                        onDismiss = { onDismiss(PermissionType.NOTIFICATION) },
                    )

                PermissionType.OVERLAY ->
                    TDOverlayPermissionItem(
                        context = context,
                        onDismiss = { onDismiss(PermissionType.OVERLAY) },
                    )
            }
        }

        if (permissions.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(permissions.size) { index ->
                    Box(
                        modifier =
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .background(
                                color =
                                if (pagerState.currentPage == index) {
                                    TDTheme.colors.pendingGray
                                } else {
                                    TDTheme.colors.onBackground.copy(alpha = 0.3f)
                                },
                                shape = CircleShape,
                            ),
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TDTheme {
        SettingsScreen(
            uiState =
            UiState(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                selectedSecretMode = SecretModeReopenOptions.Immediate,
                remainedSecretModeTime = "",
                isSecretModeActive = true,
                dailyPlanTime = LocalTime.of(9, 0),
                visiblePermissions = listOf(PermissionType.OVERLAY, PermissionType.NOTIFICATION),
            ),
            onAction = {},
            onCheckPermissions = {},
            onDismissPermission = {},
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview_Dark() {
    TDTheme {
        SettingsScreen(
            uiState =
            UiState(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                selectedSecretMode = SecretModeReopenOptions.Immediate,
                remainedSecretModeTime = "",
                isSecretModeActive = true,
                dailyPlanTime = LocalTime.of(9, 0),
                visiblePermissions = listOf(PermissionType.OVERLAY, PermissionType.NOTIFICATION),
            ),
            onAction = {},
            onCheckPermissions = {},
            onDismissPermission = {},
        )
    }
}
