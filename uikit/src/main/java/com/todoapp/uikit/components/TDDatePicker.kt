package com.todoapp.uikit.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import java.time.DayOfWeek

@Composable
fun TDDatePicker(
    selectedMonth: YearMonth,
    modifier: Modifier = Modifier,
    onMonthForwarded: () -> Unit = {},
    onMonthBacked: () -> Unit = {},
    selectedFirstDate: LocalDate? = null,
    selectedSecondDate: LocalDate? = null,
    onDaySelected: (LocalDate) -> Unit,
    onFirstDayDeselected: () -> Unit,
    onSecondDayDeselected: () -> Unit,
) {
    val daysOfWeekEntries = DayOfWeek.entries
    val days = daysOfWeekEntries.map {
        it.toString().take(2)
    }
    val daysOfWeek = days.map {
        it.lowercase().replaceFirstChar { char -> char.uppercase() }
    }
    val firstDayOfMonth = selectedMonth.atDay(1)
    val calendarStartDate =
        firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {




        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMonthBacked,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painterResource(R.drawable.outline_arrow_back_ios_new_24),
                    contentDescription = "Previous Month",
                )
            }
            Spacer(Modifier.weight(1f))
            TDText(
                text = "${
                    selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }
                } ${selectedMonth.year}",
                style = TDTheme.typography.heading5
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = onMonthForwarded,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painterResource(R.drawable.outline_arrow_forward_ios_24),
                    contentDescription = "Previous Month",
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
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TDTheme.typography.dayOfTheCalendar
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 8.dp),
            thickness = 1.dp
        )
        for (week in 0 until 5) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                for (day in 0 until 7) {
                    val currentDate = calendarStartDate.plusDays((week * 7 + day).toLong())
                    val isFromCurrentMonth =
                        currentDate.year == selectedMonth.year && currentDate.month == selectedMonth.month
                    val isCurrentDateInRange =
                        isCurrentDateInRange(selectedFirstDate, selectedSecondDate, currentDate)
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .weight(1f)
                            .background(
                                color = specifyColorBetweenRange(
                                    selectedFirstDate = selectedFirstDate,
                                    selectedSecondDate = selectedSecondDate,
                                    currentDate = currentDate
                                )
                            )
                            .padding(horizontal = 6.dp)
                            .clickable {
                                if (selectedFirstDate == currentDate) {
                                    onFirstDayDeselected()
                                } else if (selectedSecondDate == currentDate) {
                                    onSecondDayDeselected()
                                } else {
                                    onDaySelected(currentDate)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentDate.dayOfMonth.toString(),
                            color = specifyTextColor(
                                currentDate,
                                selectedFirstDate,
                                selectedSecondDate,
                                isFromCurrentMonth,
                                isCurrentDateInRange
                            ),
                            style = TDTheme.typography.subheeading4
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun specifyTextColor(
    currentDate: LocalDate?,
    selectedFirstDate: LocalDate?,
    selectedSecondDate: LocalDate?,
    isFromCurrentMonth: Boolean,
    isCurrentDateInRange: Boolean
): Color = if (currentDate == selectedFirstDate || currentDate == selectedSecondDate) {
    TDTheme.colors.white
} else if (isFromCurrentMonth) {
    TDTheme.colors.black
} else if (isCurrentDateInRange) {
    TDTheme.colors.black
} else TDTheme.colors.lightGray

@Composable
private fun isCurrentDateInRange(
    selectedFirstDate: LocalDate?,
    selectedSecondDate: LocalDate?,
    currentDate: LocalDate
): Boolean = if (selectedFirstDate != null && selectedSecondDate != null) {
    val startDate =
        if (selectedFirstDate.isBefore(selectedSecondDate)) selectedFirstDate else selectedSecondDate
    val endDate =
        if (selectedFirstDate.isAfter(selectedSecondDate)) selectedFirstDate else selectedSecondDate

    currentDate.isAfter(startDate) && currentDate.isBefore(endDate)
} else {
    false
}

@Composable
private fun specifyColorBetweenRange(
    selectedFirstDate: LocalDate?,
    selectedSecondDate: LocalDate?,
    currentDate: LocalDate,
): Color {
    return when {
        currentDate == selectedFirstDate || currentDate == selectedSecondDate -> TDTheme.colors.purple

        selectedFirstDate != null && selectedSecondDate != null &&
                currentDate.isAfter(
                    minOf(
                        selectedFirstDate,
                        selectedSecondDate
                    )
                ) &&
                currentDate.isBefore(
                    maxOf(
                        selectedFirstDate,
                        selectedSecondDate
                    )
                ) -> TDTheme.colors.lightPurple

        else -> Color.Transparent
    }
}

@Preview(showBackground = true, name = "Date Picker Preview")
@Composable
fun TDDatePickerPreview() {
    val currentMonth = YearMonth.now()
    Column {
        TDDatePicker(
            selectedMonth = currentMonth.plusMonths(2),
            onDaySelected = {},
            onFirstDayDeselected = {},
            onSecondDayDeselected = {}
        )
    }
}




