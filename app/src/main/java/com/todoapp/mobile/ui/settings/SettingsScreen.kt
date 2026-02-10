package com.todoapp.mobile.ui.settings

import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.todoapp.uikit.components.TDPlanTimePickerField
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalTime

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
        onDismissPermission = onDismissPermission
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onAction: (UiAction) -> Unit,
    onDismissPermission: (PermissionType) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        PermissionPager(
            context = context,
            permissions = uiState.visiblePermissions,
            onDismiss = onDismissPermission
        )

        Spacer(modifier = Modifier.height(12.dp))

        ThemeSelector(
            currentTheme = uiState.currentTheme,
            onThemeChange = { theme ->
                onAction(UiAction.OnThemeChange(theme))
            }
        )
        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onAction(UiAction.OnNavigateToSecretModeSettings) }
        ) {
            TDText(
                text = stringResource(R.string.privacy_security),
                style = TDTheme.typography.heading4,
                color = TDTheme.colors.onBackground
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = TDTheme.colors.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.3f))

        Spacer(modifier = Modifier.height(12.dp))

        TDPlanTimePickerField(
            title = stringResource(R.string.plan_your_day),
            subtitle = stringResource(R.string.when_do_you_want_to_get_notified),
            time = uiState.dailyPlanTime,
            onTimeChange = { onAction(UiAction.OnDailyPlanTimeChange(it)) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(color = TDTheme.colors.onBackground.copy(alpha = 0.3f))
    }
}

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
            modifier = Modifier.fillMaxWidth()
        ) { index ->
            when (permissions[index]) {
                PermissionType.NOTIFICATION -> TDNotificationPermissionItem(
                    onDismiss = { onDismiss(PermissionType.NOTIFICATION) }
                )

                PermissionType.OVERLAY -> TDOverlayPermissionItem(
                    context = context,
                    onDismiss = { onDismiss(PermissionType.OVERLAY) }
                )
            }
        }

        if (permissions.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(permissions.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .background(
                                color = if (pagerState.currentPage == index) {
                                    TDTheme.colors.primary
                                } else {
                                    TDTheme.colors.onBackground.copy(alpha = 0.3f)
                                },
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TDTheme {
        SettingsScreen(
            uiState = UiState(
                currentTheme = ThemePreference.SYSTEM_DEFAULT,
                selectedSecretMode = SecretModeReopenOptions.Immediate,
                remainedSecretModeTime = "",
                isSecretModeActive = true,
                dailyPlanTime = LocalTime.of(9, 0),
                visiblePermissions = listOf(PermissionType.OVERLAY, PermissionType.NOTIFICATION)
            ),
            onAction = {},
            onCheckPermissions = {},
            onDismissPermission = {}
        )
    }
}
