package com.todoapp.mobile.ui.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.common.RingtoneHolder
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiAction
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiEffect
import com.todoapp.mobile.ui.pomodoro.PomodoroContract.UiState
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun PomodoroScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit
) {
    PomodoroContent(
        uiState = uiState,
        uiEffect = uiEffect,
        onAction = onAction
    )
}

@Composable
private fun PomodoroContent(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit
) {
    val minuteText = "%02d".format(uiState.min)
    val secondText = "%02d".format(uiState.second)
    val context = LocalContext.current
    val ringToneHolder = remember { RingtoneHolder() }

    LaunchedEffect(uiEffect) {
        uiEffect.collect { effect ->
            when (effect) {
                is UiEffect.SessionFinished -> ringToneHolder.play(context)
            }
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(0.6f))
        Column(modifier = Modifier.height(140.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painterResource(uiState.mode.iconRes), modifier = Modifier.size(96.dp), contentDescription = "Mode")
            TDText(text = uiState.mode.title, style = TDTheme.typography.heading1)
        }
        Spacer(Modifier.weight(1f))

        TDText(
            text = minuteText,
            style = TDTheme.typography.pomodoro,
            color = uiState.mode.resolveTextColor()
        )
        TDText(
            text = secondText,
            style = TDTheme.typography.pomodoro,
            color = uiState.mode.resolveTextColor()
        )

        Spacer(Modifier.height(12.dp))

        TDText(
            text = uiState.infoMessage ?: "",
            style = TDTheme.typography.heading4,
        )
        Spacer(Modifier.height(12.dp))

        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(com.example.uikit.R.drawable.ic_more),
                    contentDescription = stringResource(R.string.more)
                )
            }
            // While overtime is active, show only the Start button so user can advance to next step.
            if (uiState.isOvertime) {
                IconButton(
                    modifier = Modifier.size(96.dp),
                    onClick = { onAction(UiAction.StartCountDown) }
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(com.example.uikit.R.drawable.ic_resume),
                        contentDescription = stringResource(R.string.next)
                    )
                }
            } else if (uiState.isStopWatchRunning) {
                IconButton(
                    modifier = Modifier.size(96.dp),
                    onClick = { onAction(UiAction.StopCountDown) }
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(com.example.uikit.R.drawable.ic_pause),
                        contentDescription = stringResource(R.string.pause)
                    )
                }
            } else {
                IconButton(
                    modifier = Modifier.size(96.dp),
                    onClick = { onAction(UiAction.StartCountDown) }
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(com.example.uikit.R.drawable.ic_resume),
                        contentDescription = stringResource(R.string.start)
                    )
                }
            }
            IconButton(onClick = { onAction(UiAction.SkipSession) }) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(com.example.uikit.R.drawable.ic_fast_forward),
                    contentDescription = stringResource(R.string.skip)
                )
            }
        }
        Spacer(Modifier.weight(1.2f))
    }
}

@Preview(
    name = "Pomodoro - Focus",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun PomodoroContentPreview_Focus() {
    TDTheme {
        PomodoroContent(
            uiState = UiState(
                min = 25,
                second = 0,
                mode = PomodoroMode.Focus
            ),
            uiEffect = emptyFlow(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Pomodoro - Short Break",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun PomodoroContentPreview_ShortBreak() {
    TDTheme {
        PomodoroContent(
            uiState = UiState(
                min = 5,
                second = 0,
                mode = PomodoroMode.ShortBreak
            ),
            uiEffect = emptyFlow(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Pomodoro - Long Break",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun PomodoroContentPreview_LongBreak() {
    TDTheme {
        PomodoroContent(
            uiState = UiState(
                min = 15,
                second = 0,
                mode = PomodoroMode.LongBreak
            ),
            uiEffect = emptyFlow(),
            onAction = {}
        )
    }
}
