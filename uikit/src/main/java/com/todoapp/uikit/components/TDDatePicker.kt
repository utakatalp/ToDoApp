package com.todoapp.uikit.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreviewWide
import com.todoapp.uikit.theme.TDTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TDDatePicker(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null,
    selectedMonth: YearMonth = YearMonth.now(),
    onMonthForward: () -> Unit = {},
    onMonthBack: () -> Unit = {},
    taskDates: Set<LocalDate> = emptySet(),
    onDaySelect: (LocalDate) -> Unit,
    onDayDeselect: () -> Unit,
) {
    // Use ComposeLocale to safely obtain the platform locale, which is more robust in Preview environments
    val locale = LocalConfiguration.current.locales[0]
    
    val daysOfWeek = remember(locale) {
        DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, locale).take(2)
        }
    }
    val firstDayOfMonth = selectedMonth.atDay(1)
    val calendarStartDate =
        firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())
    val today = LocalDate.now()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onMonthBack,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = "Previous Month",
                )
            }
            Spacer(Modifier.weight(1f))
            TDText(
                text = "${
                    selectedMonth.month.getDisplayName(TextStyle.FULL, locale)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
                } ${selectedMonth.year}",
                style = TDTheme.typography.heading5,
                color = TDTheme.colors.onBackground,
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onMonthForward,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_forward),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = "Next Month",
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            daysOfWeek.forEach { day ->
                TDText(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TDTheme.typography.dayOfTheCalendar,
                    color = TDTheme.colors.onBackground,
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
        )

        for (week in 0 until 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                for (day in 0 until 7) {
                    val currentDate = calendarStartDate.plusDays((week * 7 + day).toLong())
                    val isFromCurrentMonth =
                        currentDate.year == selectedMonth.year && currentDate.month == selectedMonth.month
                    val isSelected = currentDate == selectedDate
                    val isToday = currentDate == today
                    val hasTask = currentDate in taskDates
                    TDCalendarCell(
                        modifier = Modifier.weight(1f),
                        dayText = currentDate.dayOfMonth.toString(),
                        isSelected = isSelected,
                        isToday = isToday,
                        isFromCurrentMonth = isFromCurrentMonth,
                        hasTask = hasTask,
                        onClick = { if (isSelected) onDayDeselect() else onDaySelect(currentDate) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TDCalendarCell(
    modifier: Modifier = Modifier,
    dayText: String,
    isSelected: Boolean,
    isToday: Boolean,
    isFromCurrentMonth: Boolean,
    hasTask: Boolean,
    onClick: () -> Unit,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) TDTheme.colors.pendingGray else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "td_calendar_cell_bg",
    )

    val textColor = when {
        isSelected -> Color.White
        !isFromCurrentMonth -> TDTheme.colors.lightGray
        else -> TDTheme.colors.onBackground
    }

    Box(
        modifier = modifier
            .height(52.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isToday && !isSelected) {
                    Box(
                        Modifier
                            .size(34.dp)
                            .border(2.dp, TDTheme.colors.pendingGray, CircleShape),
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(animatedColor, CircleShape),
                )
                TDText(
                    text = dayText,
                    color = textColor,
                    style = TDTheme.typography.subheading4,
                    textAlign = TextAlign.Center,
                )
            }
            Box(
                modifier = Modifier.height(12.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                when {
                    isSelected -> Unit
                    hasTask -> Box(
                        Modifier
                            .padding(top = 3.dp)
                            .size(4.dp)
                            .background(TDTheme.colors.pendingGray, CircleShape),
                    )
                    else -> Unit
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TDDatePickerSingleInput(
    selectedMonth: YearMonth,
    modifier: Modifier = Modifier,
    onMonthForward: () -> Unit = {},
    onMonthBack: () -> Unit = {},
    selectedDate: LocalDate? = null,
    onDaySelect: (LocalDate) -> Unit,
    onDayDeselect: () -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]

    val daysOfWeek = remember(locale) {
        DayOfWeek.entries.map {
            it.getDisplayName(TextStyle.SHORT, locale).take(2)
        }
    }

    val firstDayOfMonth = selectedMonth.atDay(1)
    val calendarStartDate =
        firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TDTheme.colors.background)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMonthBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = "Previous Month",
                )
            }
            Spacer(Modifier.weight(1f))
            TDText(
                text = "${
                    selectedMonth.month
                        .getDisplayName(TextStyle.FULL, locale)
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
                } ${selectedMonth.year}",
                style = TDTheme.typography.heading5,
                color = TDTheme.colors.onBackground,
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onMonthForward, modifier = Modifier.size(40.dp)) {
                Icon(
                    painterResource(R.drawable.ic_arrow_forward),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = "Next Month",
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            daysOfWeek.forEach { day ->
                TDText(
                    text = day,
                    color = TDTheme.colors.onBackground,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TDTheme.typography.dayOfTheCalendar,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

        for (week in 0 until 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                for (day in 0 until 7) {
                    val currentDate = calendarStartDate.plusDays((week * 7 + day).toLong())
                    val isFromCurrentMonth =
                        currentDate.year == selectedMonth.year && currentDate.month == selectedMonth.month
                    val isSelected = selectedDate == currentDate
                    TDAnimatedCell(
                        modifier = Modifier.weight(1f),
                        backgroundColor = if (isSelected) TDTheme.colors.pendingGray else Color.Transparent,
                        delayMillis = 0,
                        onClick = { if (isSelected) onDayDeselect() else onDaySelect(currentDate) },
                    ) {
                        Text(
                            text = currentDate.dayOfMonth.toString(),
                            color = when {
                                !isFromCurrentMonth -> TDTheme.colors.gray.copy(alpha = 0.35f)
                                isSelected -> Color.White
                                else -> TDTheme.colors.gray
                            },
                            style = TDTheme.typography.subheading4,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TDAnimatedCell(
    modifier: Modifier,
    backgroundColor: Color,
    delayMillis: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val animatedColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 220, delayMillis = delayMillis),
        label = "td_animated_cell_bg",
    )

    Box(
        modifier = modifier
            .size(36.dp)
            .background(color = animatedColor, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@TDPreviewWide
@Composable
fun TDDatePickerPreview() {
    TDTheme {
        Column {
            TDDatePicker(
                selectedDate = LocalDate.of(2025, 3, 5),
                onDaySelect = {},
                onDayDeselect = {},
            )
        }
    }
}
