package com.todoapp.mobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

/**
 * Replacement for the normal home task card when a task is marked secret.
 * Shows a lock icon + a reveal hint; tapping opens the detail screen (which
 * requires biometrics to reveal the real content). Checkbox still toggles
 * completion without revealing anything.
 */
@Composable
fun SecretTaskRow(
    isChecked: Boolean,
    onCheckBoxClick: () -> Unit,
    onTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = TDTheme.colors.pendingGray.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
            )
            .background(TDTheme.colors.lightPending)
            .clickable(onClick = onTap)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckBoxClick() },
            colors = CheckboxDefaults.colors(
                checkedColor = TDTheme.colors.pendingGray,
                uncheckedColor = TDTheme.colors.pendingGray,
            ),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(R.drawable.ic_secret_mode),
            contentDescription = null,
            tint = TDTheme.colors.pendingGray,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        TDText(
            text = stringResource(R.string.secret_task_hint),
            style = TDTheme.typography.subheading2,
            color = TDTheme.colors.onBackground,
        )
    }
}
