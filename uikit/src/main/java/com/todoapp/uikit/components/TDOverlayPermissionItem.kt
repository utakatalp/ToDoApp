package com.todoapp.uikit.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TDText(
            text = stringResource(R.string.overlay_permission),
        )
        TDText(
            text = stringResource(R.string.allows_the_app_to_display_content_over_other_applications),
            style = TDTheme.typography.subheading1
        )
        Spacer(Modifier.height(8.dp))
        TDButton(
            isEnable = !Settings.canDrawOverlays(context),
            text = stringResource(R.string.grant_permission),
            onClick = {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
            }
        )
    }
}
