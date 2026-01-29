package com.todoapp.mobile.ui.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.uikit.components.TDGeneralProgressBar
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.components.TDWeeklyBarChart
import com.todoapp.uikit.components.TDWeeklyCircularProgressIndicator
import com.todoapp.uikit.theme.TDTheme

@Composable
fun ActivityScreen(
    uiState: UiState,
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
                text = stringResource(id = R.string.activity_screen_statistic_text),
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
                text = stringResource(id = R.string.activity_screen_activity_text),
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )

            TDWeeklyBarChart(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.activity_screen_bar_chart_component_title_text),
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
                text = "Progress",
                style = TDTheme.typography.heading2,
                color = TDTheme.colors.onBackground
            )

            TDGeneralProgressBar(
                progress = uiState.yearlyProgress
            )
        }
    }
}

@Preview("Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun ActivityScreenPreview_Light() {
    TDTheme {
        ActivityScreen(
            uiState = UiState(
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
fun ActivityScreenPreview_Dark() {
    TDTheme {
        ActivityScreen(
            uiState = UiState(
                weeklyProgress = 0.65f,
                weeklyPendingProgress = 0.35f,
                weeklyBarValues = listOf(7, 2, 3, 2, 2, 1, 4),
                yearlyProgress = 0.5f
            )
        )
    }
}
