package com.todoapp.mobile.ui.pomoodorofinish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun PomodoroFinishScreen(
    onAction: (PomodoroFinishContract.UiAction) -> Unit,
) {
    PomodoroFinishContent(
        modifier =
            Modifier
                .fillMaxSize()
                .background(TDTheme.colors.green.copy(alpha = 0.15f))
                .padding(horizontal = 16.dp),
        onAction = onAction,
    )
}

@Composable
fun PomodoroFinishContent(
    modifier: Modifier,
    onAction: (PomodoroFinishContract.UiAction) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "🎉",
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.green,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Pomodoro completed",
            style = TDTheme.typography.heading1,
            color = TDTheme.colors.green,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Nice work! What would you like to do next?",
            style = TDTheme.typography.subheading1,
            color = TDTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAction(PomodoroFinishContract.UiAction.OnRestartTap) },
        ) {
            Text(text = "Restart")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onAction(PomodoroFinishContract.UiAction.OnEditSettingsTap) },
        ) {
            Text(text = "Edit settings")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onAction(PomodoroFinishContract.UiAction.OnDismiss) },
        ) {
            Text(text = "Close")
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
fun PomodoroFinishContentPreview() {
    TDTheme {
        PomodoroFinishContent(
            modifier = Modifier
                .fillMaxSize()
                .background(TDTheme.colors.green.copy(alpha = 0.30f))
                .padding(16.dp),
            onAction = {},
        )
    }
}
