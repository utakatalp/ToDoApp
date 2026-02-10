package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.uikit.R
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import com.todoapp.uikit.theme.timePickerColors
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val HH_MM: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDPlanTimePickerField(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    time: LocalTime,
    onTimeChange: (LocalTime) -> Unit,
) {
    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
    val (initialHour, initialMinute) = remember(time) { time.hour to time.minute }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        val borderColor = TDTheme.colors.onBackground.copy(alpha = 0.12f)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(TDTheme.colors.background)
                .clickable { isDialogOpen = true }
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier.widthIn(max = 520.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(TDTheme.colors.purple.copy(alpha = 0.06f))
                            .padding(10.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = null,
                            tint = TDTheme.colors.onBackground,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = title,
                            style = TDTheme.typography.heading3,
                            color = TDTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = TDTheme.typography.regularTextStyle,
                            color = TDTheme.colors.onBackground.copy(alpha = 0.65f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(TDTheme.colors.purple.copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = time.format(HH_MM),
                        style = MaterialTheme.typography.titleSmall,
                        color = TDTheme.colors.onBackground,
                    )
                }
            }
        }

        if (isDialogOpen) {
            Dialog(onDismissRequest = { isDialogOpen = false }) {
                key(initialHour, initialMinute) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = initialHour,
                        initialMinute = initialMinute,
                        is24Hour = true,
                    )

                    Surface(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 8.dp,
                        contentColor = TDTheme.colors.onBackground,
                        color = TDTheme.colors.background,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(max = 320.dp)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            TimeInput(
                                state = timePickerState,
                                colors = timePickerColors(),
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TDButton(
                                    text = stringResource(R.string.cancel),
                                    onClick = { isDialogOpen = false },
                                    size = TDButtonSize.SMALL,
                                    type = TDButtonType.SECONDARY,
                                )

                                TDButton(
                                    text = stringResource(R.string.ok),
                                    onClick = {
                                        onTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
                                        isDialogOpen = false
                                    },
                                    size = TDButtonSize.SMALL,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@TDPreview
@Composable
fun TDPlanTimePicker_DefaultPreview() {
    TDTheme {
        val timeState = remember { mutableStateOf(LocalTime.of(9, 0)) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TDPlanTimePickerField(
                title = "Plan your day",
                subtitle = "When do you want to start your day?",
                time = timeState.value,
                onTimeChange = { timeState.value = it },
            )
        }
    }
}

@TDPreview
@Composable
fun TDPlanTimePicker_SelectedPreview() {
    TDTheme {
        val timeState = remember { mutableStateOf(LocalTime.of(14, 30)) }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TDPlanTimePickerField(
                title = "Plan your day",
                subtitle = "When do you want to start your day?",
                time = timeState.value,
                onTimeChange = { timeState.value = it },
            )
        }
    }
}
