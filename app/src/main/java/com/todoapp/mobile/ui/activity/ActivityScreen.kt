package com.todoapp.mobile.ui.activity

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
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDGeneralProgressBar
import com.todoapp.uikit.components.TDLoadingBar
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWeeklyBarChart
import com.todoapp.uikit.components.TDWeeklyCircularProgressIndicator
import com.todoapp.uikit.theme.TDTheme

@Composable
fun ActivityScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    when (uiState) {
        is UiState.Loading -> {
            ActivityLoadingContent()
        }

        is UiState.Error -> {
            ActivityErrorContent(
                message = uiState.message,
                onAction = onAction
            )
        }

        is UiState.Success -> {
            ActivitySuccessContent(uiState = uiState)
        }
    }
}

@Composable
private fun ActivityLoadingContent() {
    TDLoadingBar()
}

@Composable
private fun ActivityErrorContent(
    message: String,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            tint = TDTheme.colors.crossRed,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        TDText(
            text = message,
            style = TDTheme.typography.heading3,
            color = TDTheme.colors.onBackground
        )
        Spacer(Modifier.height(24.dp))
        TDButton(
            text = stringResource(com.todoapp.mobile.R.string.retry),
            onClick = { onAction(UiAction.OnRetry) },
            size = TDButtonSize.SMALL
        )
    }
}

@Composable
private fun ActivitySuccessContent(
    uiState: UiState.Success,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TDText(
                text = stringResource(id = com.todoapp.mobile.R.string.activity_screen_statistic_text),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            TDWeeklyCircularProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                strokeWidth = 16.dp,
                strokeCap = Butt,
                progress = uiState.weeklyProgress,
                inProgress = uiState.weeklyPendingProgress,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TDText(
                text = stringResource(id = com.todoapp.mobile.R.string.activity_screen_activity_text),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
            TDWeeklyBarChart(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = com.todoapp.mobile.R.string.activity_screen_bar_chart_component_title_text),
                values = uiState.weeklyBarValues,
                height = 220.dp,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, end = 16.dp, start = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.activity_screen_progress_text),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )
            TDGeneralProgressBar(
                progress = uiState.yearlyProgress
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityLoadingPreview() {
    TDTheme {
        ActivityLoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityErrorPreview() {
    TDTheme {
        ActivityErrorContent(
            message = "Something went wrong",
            onAction = {}
        )
    }
}

@Preview("Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun ActivityScreenPreview_Light() {
    TDTheme {
        ActivitySuccessContent(
            uiState = UiState.Success(
                weeklyProgress = 0.65f,
                weeklyPendingProgress = 0.35f,
                weeklyBarValues = listOf(7, 2, 3, 2, 5, 1, 4),
                yearlyProgress = 0.5f
            )
        )
    }
}

@Preview("Night", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun ActivityScreenPreview_Dark() {
    TDTheme {
        ActivitySuccessContent(
            uiState = UiState.Success(
                weeklyProgress = 0.65f,
                weeklyPendingProgress = 0.35f,
                weeklyBarValues = listOf(7, 2, 3, 2, 2, 1, 4),
                yearlyProgress = 0.5f
            )
        )
    }
}
