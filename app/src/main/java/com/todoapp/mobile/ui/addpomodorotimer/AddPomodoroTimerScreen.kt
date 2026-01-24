package com.todoapp.mobile.ui.addpomodorotimer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiAction
import com.todoapp.mobile.ui.addpomodorotimer.AddPomodoroTimerContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun AddPomodoroTimerScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit
) {
    AddPomodoroTimerContent(
        uiState,
        onAction
    )
}

@Composable
private fun AddPomodoroTimerContent(
    uiState: UiState,
    onAction: (UiAction) -> Unit
) {
    Column(

        Modifier
            .background(TDTheme.colors.background)
            .statusBarsPadding()
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PomodoroInputCard(
            selected = uiState.sessionCount,
            modifier = Modifier.height(80.dp),
            sectionMode = true,
            label = "Session Count",
            onSelectedChange = { onAction(UiAction.onSessionCountChange(it)) }
        )
        PomodoroInputCard(
            selected = uiState.focusTime,
            modifier = Modifier.height(80.dp),
            label = "Focus Time",
            onSelectedChange = { onAction(UiAction.onFocusTimeChange(it)) }
        )
        PomodoroInputCard(
            selected = uiState.shortBreak,
            modifier = Modifier.height(80.dp),
            label = "Short Break",
            onSelectedChange = { onAction(UiAction.onShortBreakChange(it)) }
        )
        PomodoroInputCard(
            selected = uiState.longBreak,
            modifier = Modifier.height(80.dp),
            label = "Long Break",
            onSelectedChange = { onAction(UiAction.onLongBreakChange(it)) }
        )
        PomodoroInputCard(
            selected = uiState.sectionCount,
            modifier = Modifier.height(80.dp),
            sectionMode = true,
            label = "Section Count",
            onSelectedChange = { onAction(UiAction.onSectionCountChange(it)) }
        )
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth()) {
            TDButton(modifier = Modifier.weight(1f), text = "Cancel", type = TDButtonType.CANCEL) {
                onAction(UiAction.OnCancelTap)
            }
            Spacer(Modifier.width(25.dp))
            TDButton(modifier = Modifier.weight(1f), text = "Start", type = TDButtonType.PRIMARY) {
                onAction(UiAction.OnStartTap)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PomodoroInputCard(
    modifier: Modifier = Modifier,
    selected: Float,
    sectionMode: Boolean = false,
    label: String,
    onSelectedChange: (Float) -> Unit,
) {
    var expand by rememberSaveable { mutableStateOf(false) }
    val valueRange = if (sectionMode) 1f..15f else 0f..120f
    val steps = (valueRange.endInclusive - valueRange.start + 1).toInt()

    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(TDTheme.colors.lightPurple),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(32.dp))
            TDText(text = label, style = TDTheme.typography.heading2)
            Spacer(Modifier.weight(8f))
            TDText(
                text = if (sectionMode) "${selected.toInt()} intervals" else "${selected.toInt()} min",
                style = TDTheme.typography.heading4,
                color = TDTheme.colors.primary
            )
            Spacer(Modifier.weight(0.1f))
            IconButton(onClick = { expand = !expand }) {
                if (expand) {
                    Icon(painterResource(R.drawable.ic_arrow_up), contentDescription = "Change Time")
                } else {
                    Icon(painterResource(R.drawable.ic_arrow_down), contentDescription = "Change Time")
                }
            }
            Spacer(Modifier.weight(0.6f))
        }
        AnimatedVisibility(
            visible = expand,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 220)
            ) + fadeIn(animationSpec = tween(durationMillis = 220)),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 180)
            ) + fadeOut(animationSpec = tween(durationMillis = 180)),
        ) {
            Slider(
                value = selected,
                onValueChange = onSelectedChange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = TDTheme.colors.primary,
                    activeTrackColor = TDTheme.colors.purple,
                    inactiveTickColor = TDTheme.colors.lightPurple
                ),
                valueRange = valueRange
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PomodoroInputCardPreview() {
    var selected by remember { mutableStateOf(25f) }

    PomodoroInputCard(
        modifier = Modifier
            .padding(vertical = 32.dp)
            .height(80.dp),
        selected = selected,
        onSelectedChange = { selected = it },
        label = "Focus Time"
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun AddPomodoroTimerContentPreview() {
    val uiState = UiState()

    TDTheme {
        AddPomodoroTimerContent(
            uiState = uiState,
            onAction = {}
        )
    }
}
