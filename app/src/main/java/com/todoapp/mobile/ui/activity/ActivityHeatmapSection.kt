package com.todoapp.mobile.ui.activity

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.activity.ActivityContract.UiAction
import com.todoapp.mobile.ui.activity.ActivityContract.UiState
import com.todoapp.uikit.components.TDActivityHeatmap
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun ActivityHeatmapSection(
    state: UiState.Success,
    @Suppress("UNUSED_PARAMETER") onAction: (UiAction) -> Unit,
) {
    var tooltipDate by remember(state.selectedMonth) { mutableStateOf<LocalDate?>(null) }
    val locale = currentLocale()
    val formatter = remember(locale) { DateTimeFormatter.ofPattern("MMM d", locale) }

    val monthName = remember(state.selectedMonth, locale) {
        state.selectedMonth.month.getDisplayName(java.time.format.TextStyle.FULL, locale)
    }

    ActivityCard {
        TDActivityHeatmap(
            startDate = state.heatmapStartOrFallback(),
            endDate = state.heatmapEndOrFallback(),
            counts = state.heatmapData,
            onCellClick = { date -> tooltipDate = date },
            title = stringResource(R.string.activity_heatmap_month_title, monthName),
            legendLessLabel = stringResource(R.string.activity_heatmap_legend_less),
            legendMoreLabel = stringResource(R.string.activity_heatmap_legend_more),
            singleMonthMode = true,
        )

        if (tooltipDate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            val count = state.heatmapData[tooltipDate] ?: 0
            TDText(
                text = stringResource(
                    R.string.activity_heatmap_tooltip,
                    formatter.format(tooltipDate),
                    count,
                ),
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.onBackground,
            )
        }
    }
}

private fun UiState.Success.heatmapStartOrFallback(): LocalDate = selectedMonth.atDay(1)

private fun UiState.Success.heatmapEndOrFallback(): LocalDate = selectedMonth.atEndOfMonth()
