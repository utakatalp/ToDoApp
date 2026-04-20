package com.todoapp.mobile.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.uikit.R
import com.todoapp.mobile.ui.home.HomeContract.UiAction
import com.todoapp.mobile.ui.home.HomeContract.UiEffect
import com.todoapp.mobile.ui.home.HomeContract.UiState
import com.todoapp.mobile.ui.security.biometric.BiometricAuthenticator
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDScreenWithSheet
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow

@Composable
fun HomeScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current

    uiEffect.collectWithLifecycle {
        when (it) {
            is UiEffect.ShowToast -> {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }

            is UiEffect.ShowBiometricAuthenticator -> {
                handleBiometricAuthentication(context) {
                    onAction(UiAction.OnSuccessfulBiometricAuthenticationHandle)
                }
            }

            is UiEffect.ShowBiometricForSecretToggle -> {
                val task = it.task
                handleBiometricAuthentication(context) {
                    onAction(UiAction.OnBiometricSuccessForSecretToggle(task))
                }
            }

            is UiEffect.ShowError -> TODO()
        }
    }

    when (uiState) {
        is UiState.Loading -> HomeLoadingContent()
        is UiState.Error -> HomeErrorContent(message = uiState.message, onAction = onAction)
        is UiState.Success -> HomeSuccessContent(uiState = uiState, onAction = onAction)
    }
}

@Composable
private fun HomeLoadingContent() {
    TDLoadingBar()
}

@Composable
private fun HomeErrorContent(
    message: String,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = TDTheme.colors.crossRed,
            modifier = Modifier.size(64.dp),
        )
        Spacer(Modifier.height(16.dp))
        TDText(
            text = message,
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onBackground,
        )
        Spacer(Modifier.height(24.dp))
        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.retry),
            onClick = { onAction(UiAction.OnRetry) },
            size = TDButtonSize.SMALL,
        )
    }
}

@Composable
private fun HomeSuccessContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    TDScreenWithSheet(
        isSheetOpen = uiState.isSheetOpen,
        sheetContent = {
            AddTaskSheet(
                formState = uiState.taskFormState,
                availableGroups = uiState.availableGroups,
                onAction = { action ->
                    when (action) {
                        is TaskFormUiAction.Dismiss -> onAction(UiAction.OnDismissBottomSheet)
                        is TaskFormUiAction.Create -> onAction(UiAction.OnTaskCreate)
                        is TaskFormUiAction.TitleChange -> onAction(UiAction.OnTaskTitleChange(action.title))
                        is TaskFormUiAction.DateSelect -> onAction(UiAction.OnDialogDateSelect(action.date))
                        is TaskFormUiAction.DateDeselect -> onAction(UiAction.OnDialogDateDeselect)
                        is TaskFormUiAction.TimeStartChange -> onAction(UiAction.OnTaskTimeStartChange(action.time))
                        is TaskFormUiAction.TimeEndChange -> onAction(UiAction.OnTaskTimeEndChange(action.time))
                        is TaskFormUiAction.DescriptionChange ->
                            onAction(
                                UiAction.OnTaskDescriptionChange(action.description),
                            )
                        is TaskFormUiAction.ToggleAdvancedSettings -> onAction(UiAction.OnToggleAdvancedSettings)
                        is TaskFormUiAction.SecretChange -> onAction(UiAction.OnTaskSecretChange(action.isSecret))
                        is TaskFormUiAction.GroupSelectionChanged ->
                            onAction(
                                UiAction.OnGroupSelectionChanged(action.groupId),
                            )
                        is TaskFormUiAction.PriorityChange -> Unit
                        is TaskFormUiAction.AssigneeChange -> Unit
                        is TaskFormUiAction.PhotoPicked ->
                            onAction(
                                UiAction.OnPendingPhotoAdd(action.bytes, action.mimeType),
                            )
                        is TaskFormUiAction.PhotoRemoveAt ->
                            onAction(
                                UiAction.OnPendingPhotoRemove(action.index),
                            )
                        is TaskFormUiAction.ExistingPhotoToggleDelete -> Unit
                    }
                },
            )
        },
        onDismissSheet = { onAction(UiAction.OnDismissBottomSheet) },
    ) {
        HomeContent(
            modifier =
            Modifier
                .fillMaxWidth()
                .background(TDTheme.colors.background)
                .padding(horizontal = 16.dp),
            uiState = uiState,
            onAction = onAction,
        )
    }
}

private suspend fun handleBiometricAuthentication(
    context: Context,
    onSuccess: () -> Unit,
) {
    val activity = context as? FragmentActivity ?: return
    val isAuthenticated = BiometricAuthenticator.authenticate(activity)
    if (isAuthenticated) onSuccess()
}

@Preview(showBackground = true)
@Composable
private fun HomeLoadingPreview() {
    TDTheme {
        HomeLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeErrorPreview() {
    TDTheme {
        HomeErrorContent(
            message = "Something went wrong",
            onAction = {},
        )
    }
}
