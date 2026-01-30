package com.todoapp.uikit.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDOverlayPermissionItem(
    context: Context,
    initialGranted: Boolean = Settings.canDrawOverlays(context)
) {
    var granted by remember { mutableStateOf(initialGranted) }
    var dismissed by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                granted = Settings.canDrawOverlays(context)
                if (!granted) {
                    dismissed = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (granted || dismissed) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TDText(
            text = stringResource(R.string.overlay_permission),
            style = TDTheme.typography.heading1
        )
        TDText(
            text = stringResource(R.string.allows_the_app_to_display_content_over_other_applications),
            style = TDTheme.typography.subheading1
        )
        Spacer(Modifier.height(8.dp))
        TDButton(
            modifier = Modifier.fillMaxWidth(),
            isEnable = !Settings.canDrawOverlays(context),
            text = stringResource(R.string.grant_permission),
            onClick = {
                val packageUri = "package:${context.packageName}".toUri()

                val overlayIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, packageUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                runCatching {
                    context.startActivity(overlayIntent)
                }.getOrElse {
                    context.startActivity(appDetailsIntent)
                }
            }
        )
    }
}
