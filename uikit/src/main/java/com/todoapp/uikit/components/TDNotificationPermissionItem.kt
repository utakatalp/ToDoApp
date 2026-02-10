package com.todoapp.uikit.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDNotificationPermissionItem() {
    val context = LocalContext.current
    val permission = android.Manifest.permission.POST_NOTIFICATIONS

    val shouldOpenSettingsOnNextGrantClick = remember {
        mutableStateOf(false)
    }

    val isNotificationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val activity = context as? android.app.Activity
            val shouldShowRationale = activity?.let {
                androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: true

            if (!shouldShowRationale) {
                shouldOpenSettingsOnNextGrantClick.value = true
            }
        } else if (isGranted) {
            shouldOpenSettingsOnNextGrantClick.value = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TDText(
            text = stringResource(R.string.notification_permission),
            style = TDTheme.typography.heading1
        )
        TDText(
            text = stringResource(R.string.allows_the_app_to_display_notifications),
            style = TDTheme.typography.subheading1
        )
        Spacer(Modifier.height(8.dp))
        TDButton(
            fullWidth = true,
            text = stringResource(R.string.grant_permission),
            isEnable = !isNotificationPermissionGranted,
            onClick = {
                if (shouldOpenSettingsOnNextGrantClick.value) {
                    openNotificationSettings(context)
                    return@TDButton
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return@TDButton

                val granted = ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED

                if (!granted) {
                    launcher.launch(permission)
                }
            }
        )
    }
}

fun openNotificationSettings(context: Context) {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}
