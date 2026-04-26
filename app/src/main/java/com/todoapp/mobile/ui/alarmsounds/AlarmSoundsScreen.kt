package com.todoapp.mobile.ui.alarmsounds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.repository.AlarmSoundOption
import com.todoapp.mobile.ui.alarmsounds.AlarmSoundsContract.UiAction
import com.todoapp.mobile.ui.alarmsounds.AlarmSoundsContract.UiState
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.modifier.neumorphicShadow
import com.todoapp.uikit.theme.TDTheme

@Composable
fun AlarmSoundsScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background),
    ) {
        when (uiState) {
            is UiState.Loading -> Loading()
            is UiState.Error -> ErrorBlock(uiState.message)
            is UiState.Success -> if (uiState.items.isEmpty()) {
                EmptyBlock()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(items = uiState.items, key = { it.uri.toString() }) { option ->
                        AlarmSoundRow(
                            option = option,
                            isSelected = option.uri == uiState.selectedUri,
                            onClick = { onAction(UiAction.OnSelect(option.uri)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Loading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TDTheme.colors.purple)
    }
}

@Composable
private fun EmptyBlock() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TDText(
            text = stringResource(R.string.alarm_sounds_empty),
            style = TDTheme.typography.subheading2,
            color = TDTheme.colors.gray,
        )
    }
}

@Composable
private fun ErrorBlock(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TDText(
            text = message,
            style = TDTheme.typography.subheading2,
            color = TDTheme.colors.crossRed,
        )
    }
}

@Composable
private fun AlarmSoundRow(
    option: AlarmSoundOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val cardModifier = Modifier
        .fillMaxWidth()
        .let { base ->
            if (isDark) {
                base.border(
                    width = 1.dp,
                    color = TDTheme.colors.lightGray.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(16.dp),
                )
            } else {
                base.neumorphicShadow(
                    lightShadow = TDTheme.colors.white.copy(alpha = 0.85f),
                    darkShadow = TDTheme.colors.lightGray.copy(alpha = 0.30f),
                    cornerRadius = 16.dp,
                    elevation = 6.dp,
                )
            }
        }
        .clip(RoundedCornerShape(16.dp))
        .background(if (isSelected) TDTheme.colors.bgColorPurple else TDTheme.colors.lightPending)
        .clickable(onClick = onClick)
        .padding(14.dp)

    Row(modifier = cardModifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) TDTheme.colors.purple else TDTheme.colors.bgColorPurple),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(com.example.uikit.R.drawable.ic_clock),
                contentDescription = null,
                tint = if (isSelected) TDTheme.colors.white else TDTheme.colors.purple,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            TDText(
                text = option.title,
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.onBackground,
            )
        }
        if (isSelected) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(TDTheme.colors.purple),
            )
        }
    }
}
