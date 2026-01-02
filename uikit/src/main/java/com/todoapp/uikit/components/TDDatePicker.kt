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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TDDatePicker(
    modifier: Modifier = Modifier,
    selectedFirstDate: LocalDate? = null,
    selectedSecondDate: LocalDate? = null,
    onFirstDaySelect: (LocalDate) -> Unit,
    onSecondDaySelect: (LocalDate) -> Unit,
    onFirstDayDeselect: () -> Unit,
    onSecondDayDeselect: () -> Unit,
) {
    var selectedMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    val daysOfWeekEntries = DayOfWeek.entries
    val days =
        daysOfWeekEntries.map {
            it.toString().take(2)
        }
    val daysOfWeek =
        days.map {
            it.lowercase().replaceFirstChar { char -> char.uppercase() }
        }
    val firstDayOfMonth = selectedMonth.atDay(1)
    val calendarStartDate =
        firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { selectedMonth = selectedMonth.minusMonths(1) },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Previous Month",
                )
            }
            Spacer(Modifier.weight(1f))
            TDText(
                text = "${
                    selectedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }
                } ${selectedMonth.year}",
                style = TDTheme.typography.heading5,
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { selectedMonth = selectedMonth.plusMonths(1) },
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = "Next Month",
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            daysOfWeek.forEach { day ->
                TDText(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TDTheme.typography.dayOfTheCalendar,
                )
            }
        }
        HorizontalDivider(
            modifier =
                Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
        )
        for (week in 0 until 5) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                for (day in 0 until 7) {
                    val currentDate = calendarStartDate.plusDays((week * 7 + day).toLong())
                    val isFromCurrentMonth =
                        currentDate.year == selectedMonth.year && currentDate.month == selectedMonth.month
                    val isCurrentDateInRange =
                        isCurrentDateInRange(selectedFirstDate, selectedSecondDate, currentDate)
                    Box(
                        modifier =
                            Modifier
                                .height(30.dp)
                                .weight(1f)
                                .background(
                                    color =
                                        specifyColorBetweenRange(
                                            selectedFirstDate = selectedFirstDate,
                                            selectedSecondDate = selectedSecondDate,
                                            currentDate = currentDate,
                                        ),
                                ).padding(horizontal = 6.dp)
                                .clickable {
                                    when {
                                        currentDate == selectedFirstDate -> {
                                            onFirstDayDeselect()
                                        }
                                        currentDate == selectedSecondDate -> {
                                            onSecondDayDeselect()
                                        }
                                        selectedFirstDate == null -> {
                                            onFirstDaySelect(currentDate)
                                        }
                                        selectedSecondDate == null -> {
                                            if (currentDate < selectedFirstDate) {
                                                onFirstDaySelect(currentDate)
                                            } else {
                                                onSecondDaySelect(currentDate)
                                            }
                                        }
                                        else -> {
                                            if (currentDate < selectedFirstDate) {
                                                onFirstDaySelect(currentDate)
                                            } else if (currentDate > selectedFirstDate) {
                                                onSecondDaySelect(currentDate)
                                            }
                                        }
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = currentDate.dayOfMonth.toString(),
                            color =
                                specifyTextColor(
                                    currentDate,
                                    selectedFirstDate,
                                    selectedSecondDate,
                                    isFromCurrentMonth,
                                    isCurrentDateInRange,
                                ),
                            style = TDTheme.typography.subheading4,
                        )
                    }
                }
            }
        }
    }
}

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
    val daysOfWeekEntries = DayOfWeek.entries
    val days = daysOfWeekEntries.map { it.toString().take(2) }
    val daysOfWeek = days.map { it.lowercase().replaceFirstChar { ch -> ch.uppercase() } }

    val firstDayOfMonth = selectedMonth.atDay(1)
    val calendarStartDate =
        firstDayOfMonth.minusDays((firstDayOfMonth.dayOfWeek.value - 1).toLong())

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMonthBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Previous Month",
                )
            }

            Spacer(Modifier.weight(1f))

            TDText(
                text = "${
                    selectedMonth.month
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() }
                } ${selectedMonth.year}",
                style = TDTheme.typography.heading5,
            )

            Spacer(Modifier.weight(1f))

            IconButton(onClick = onMonthForward, modifier = Modifier.size(40.dp)) {
                Icon(
                    painterResource(R.drawable.ic_arrow_forward),
                    contentDescription = "Next Month",
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
        ) {
            daysOfWeek.forEach { day ->
                TDText(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = TDTheme.typography.dayOfTheCalendar,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp)

        for (week in 0 until 5) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
            ) {
                for (day in 0 until 7) {
                    val currentDate = calendarStartDate.plusDays((week * 7 + day).toLong())
                    val isFromCurrentMonth =
                        currentDate.year == selectedMonth.year && currentDate.month == selectedMonth.month

                    val isSelected = selectedDate == currentDate

                    Box(
                        modifier =
                            Modifier
                                .height(30.dp)
                                .weight(1f)
                                .padding(horizontal = 6.dp)
                                .background(
                                    color = if (isSelected) TDTheme.colors.purple else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp),
                                ).clickable {
                                    if (isSelected) onDayDeselect() else onDaySelect(currentDate)
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = currentDate.dayOfMonth.toString(),
                            color =
                                when {
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
private fun specifyTextColor(
    currentDate: LocalDate?,
    selectedFirstDate: LocalDate?,
    selectedSecondDate: LocalDate?,
    isFromCurrentMonth: Boolean,
    isCurrentDateInRange: Boolean,
): Color =
    if (currentDate == selectedFirstDate || currentDate == selectedSecondDate) {
        TDTheme.colors.white
    } else if (isFromCurrentMonth) {
        TDTheme.colors.black
    } else if (isCurrentDateInRange) {
        TDTheme.colors.black
    } else {
        TDTheme.colors.lightGray
    }

@Composable
private fun isCurrentDateInRange(
    selectedFirstDate: LocalDate?,
    selectedSecondDate: LocalDate?,
    currentDate: LocalDate,
): Boolean =
    if (selectedFirstDate != null && selectedSecondDate != null) {
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
): Color =
    when {
        currentDate == selectedFirstDate || currentDate == selectedSecondDate -> TDTheme.colors.purple

        selectedFirstDate != null &&
            selectedSecondDate != null &&
            currentDate.isAfter(
                minOf(
                    selectedFirstDate,
                    selectedSecondDate,
                ),
            ) &&
            currentDate.isBefore(
                maxOf(
                    selectedFirstDate,
                    selectedSecondDate,
                ),
            ) -> TDTheme.colors.lightPurple

        else -> Color.Transparent
    }

@Preview(showBackground = true, name = "Date Picker Preview")
@Composable
private fun TDDatePickerPreview() {
    Column {
        TDDatePicker(
            onFirstDaySelect = {},
            onSecondDaySelect = {},
            onFirstDayDeselect = {},
        ) {}
    }
}
