package com.todoapp.mobile.ui.home

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.permissions.NotificationPermissionPrompt
import com.todoapp.mobile.ui.permissions.OverlayPermissionPrompt
import com.todoapp.mobile.ui.settings.PermissionType

@Composable
internal fun HomePermissionPrompts(
    permissions: List<PermissionType>,
    onAction: (UiAction) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(bottom = 12.dp),
    ) {
        permissions.forEach { type ->
            AnimatedVisibility(
                visible = true,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                when (type) {
                    PermissionType.NOTIFICATION ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            NotificationPermissionPrompt(
                                onGranted = { onAction(UiAction.PermissionGranted(type)) },
                                onDismiss = { onAction(UiAction.DismissPermission(type)) },
                            )
                        }
                    PermissionType.OVERLAY ->
                        OverlayPermissionPrompt(
                            onGranted = { onAction(UiAction.PermissionGranted(type)) },
                            onDismiss = { onAction(UiAction.DismissPermission(type)) },
                        )
                }
            }
        }
    }
}
