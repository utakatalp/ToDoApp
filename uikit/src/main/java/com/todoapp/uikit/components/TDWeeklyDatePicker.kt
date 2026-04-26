package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.previews.TDPreviewWide
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max

@Composable
fun TDWeeklyDatePicker(
    modifier: Modifier,
    displayedMonth: YearMonth,
    selectedDate: LocalDate? = LocalDate.now(),
    onDateSelect: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val daysInMonth = displayedMonth.lengthOfMonth()
    val listState = rememberLazyListState()

    LaunchedEffect(displayedMonth, selectedDate) {
        val scrollIndex =
            max(
                0,
                selectedDate
                    ?.takeIf { YearMonth.from(it) == displayedMonth }
                    ?.dayOfMonth
                    ?.minus(4)
                    ?: 0,
            )
        listState.animateScrollToItem(scrollIndex)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        MonthNavigationHeader(
            displayedMonth = displayedMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
        )
        LazyRow(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            items(daysInMonth) { i ->
                DatePickerCard(
                    modifier = Modifier,
                    currentDate = displayedMonth.atDay(i + 1),
                    isSelected = selectedDate == displayedMonth.atDay(i + 1),
                    onDateSelect = onDateSelect,
                )
            }
        }
    }
}

@Composable
private fun MonthNavigationHeader(
    displayedMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    // Use Locale.getDefault() as a fallback to avoid rendering issues in Previews where locales might be empty
    val configuration = LocalConfiguration.current
    val locale = if (!configuration.locales.isEmpty) configuration.locales[0] else Locale.getDefault()
    val monthLabel = displayedMonth.month.getDisplayName(TextStyle.FULL, locale)
    val yearLabel = displayedMonth.year.toString()

    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Previous month",
                tint = TDTheme.colors.onBackground,
            )
        }
        TDText(
            text = "$monthLabel $yearLabel",
            style = TDTheme.typography.heading4,
            color = TDTheme.colors.onBackground,
        )
        IconButton(onClick = onNextMonth) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = "Next month",
                tint = TDTheme.colors.onBackground,
            )
        }
    }
}

@Composable
private fun DatePickerCard(
    modifier: Modifier,
    currentDate: LocalDate,
    isSelected: Boolean,
    onDateSelect: (LocalDate) -> Unit = {},
) {
    val textColor = if (isSelected) TDTheme.colors.white else TDTheme.colors.lightGray
    Column(
        modifier =
        modifier
            .background(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) TDTheme.colors.pendingGray.copy(alpha = 0.8f) else Color.Transparent,
            ).size(width = 48.dp, height = 80.dp)
            .clickable(
                onClick = { onDateSelect(currentDate) },
            ).padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier.weight(0.8f))
        TDText(
            text =
            currentDate.dayOfWeek.toString().take(3).let {
                it[0] + it[1].lowercase() + it[2].lowercase()
            },
            style = TDTheme.typography.regularTextStyle,
            color = textColor,
        )
        Spacer(modifier.weight(0.2f))
        TDText(
            text = currentDate.dayOfMonth.toString(),
            style = TDTheme.typography.heading4,
            color = textColor,
        )
        Spacer(modifier.weight(1f))
        if (isSelected) {
            Icon(
                modifier = Modifier.size(8.dp),
                painter = painterResource(R.drawable.ic_outlined_circle),
                contentDescription = "Today",
                tint = TDTheme.colors.white,
            )
            Spacer(modifier.weight(0.3f))
        }
    }
}

@TDPreviewWide
@Composable
fun WeeklyDatePickerPreview() {
    TDTheme {
        var selected by remember {
            mutableStateOf(LocalDate.of(2025, 12, 3))
        }

        TDWeeklyDatePicker(
            modifier = Modifier,
            displayedMonth = YearMonth.of(2025, 12),
            selectedDate = selected,
            onDateSelect = { selected = it },
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}

@TDPreview
@Composable
fun DatePickerCardSelectedPreview() {
    TDTheme {
        DatePickerCard(
            modifier = Modifier,
            currentDate = LocalDate.of(2025, 12, 17),
            isSelected = true,
        )
    }
}

@TDPreview
@Composable
fun DatePickerCardUnselectedPreview() {
    TDTheme {
        DatePickerCard(
            modifier = Modifier,
            currentDate = LocalDate.of(2025, 12, 18),
            isSelected = false,
        )
    }
}
