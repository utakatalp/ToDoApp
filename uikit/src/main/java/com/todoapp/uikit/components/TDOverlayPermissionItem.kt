package com.todoapp.uikit.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDOverlayPermissionItem(
    context: Context
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth(),
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
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    "package:${context.packageName}".toUri()
                )
                context.startActivity(intent)
            }
        )
    }
}
