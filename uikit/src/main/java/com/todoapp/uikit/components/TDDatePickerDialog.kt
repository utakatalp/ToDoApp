package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.uikit.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun TDDatePickerDialog(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = LocalDate.now(),
    onDateSelect: (LocalDate) -> Unit,
    onDateDeselect: () -> Unit,
) {
    var selectedMonth by rememberSaveable { mutableStateOf(YearMonth.now()) }
    var isPickerOpen by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
    ) {
        TDPickerField(
            title = stringResource(R.string.pick_a_date),
            value =
                selectedDate?.format(
                    DateTimeFormatter.ofPattern(
                        "dd MMMM yyyy",
                    ),
                ) ?: stringResource(R.string.pick_a_date),
            onClick = { isPickerOpen = true },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar2),
                    contentDescription = null,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (isPickerOpen) {
            Dialog(
                onDismissRequest = { isPickerOpen = false },
            ) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                ) {
                    Column {
                        TDDatePickerSingleInput(
                            selectedMonth = selectedMonth,
                            selectedDate = selectedDate,
                            onMonthBack = { selectedMonth = selectedMonth.minusMonths(1) },
                            onMonthForward = { selectedMonth = selectedMonth.plusMonths(1) },
                            onDaySelect = onDateSelect,
                            onDayDeselect = onDateDeselect,
                        )
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            TDButton(
                                text = stringResource(R.string.ok),
                                onClick = { isPickerOpen = false },
                                size = TDButtonSize.SMALL,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun TDDatePickerDialogPreview() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    TDDatePickerDialog(
        selectedDate = selectedDate,
        onDateSelect = { selectedDate = it },
        onDateDeselect = { selectedDate = null },
    )
}
