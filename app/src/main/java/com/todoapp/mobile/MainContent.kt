package com.todoapp.mobile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.todoapp.mobile.R
import com.todoapp.mobile.MainContract.UiAction.OnDialogOkTap
import com.todoapp.mobile.navigation.NavigationEffectController
import com.todoapp.mobile.navigation.ThemedApp
import com.todoapp.uikit.extensions.collectWithLifecycle

@Composable
fun MainContent() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        val mainViewModel: MainViewModel = hiltViewModel()

        var dialogMessage by rememberSaveable { mutableStateOf<String?>(null) }

        mainViewModel.uiEffect.collectWithLifecycle { effect ->
            when (effect) {
                is MainContract.UiEffect.ShowDialog -> {
                    dialogMessage = effect.message
                }
            }
        }

        NavigationEffectController(mainViewModel.navEffect)

        MainDialog(
            message = dialogMessage,
            onOk = {
                dialogMessage = null
                mainViewModel.onAction(OnDialogOkTap)
            },
        )

        ThemedApp()
    }
}

@Composable
private fun MainDialog(
    message: String?,
    onOk: () -> Unit,
) {
    if (message == null) return

    AlertDialog(
        onDismissRequest = onOk,
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onOk) {
                Text(stringResource(R.string.ok))
            }
        },
    )
}
