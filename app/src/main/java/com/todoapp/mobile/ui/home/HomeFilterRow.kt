package com.todoapp.mobile.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.ui.home.HomeContract.HomeFilter
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.modifier.neumorphicShadow
import com.todoapp.uikit.theme.TDTheme

/**
 * Top-of-screen chip row for switching between Today / Daily / Weekly / Monthly / Yearly views.
 * Mirrors the TDCategoryPicker / TDRecurrencePicker chip styling — selected = purple, unselected
 * = lightPending, light-mode neumorphic shadow + dark-mode subtle border.
 */
@Composable
internal fun HomeFilterRow(
    selected: HomeFilter,
    onSelected: (HomeFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = HomeFilter.values().toList(), key = { it.name }) { filter ->
            FilterChip(
                filter = filter,
                isSelected = filter == selected,
                onClick = { onSelected(filter) },
            )
        }
    }
}

@Composable
private fun FilterChip(
    filter: HomeFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val background = if (isSelected) TDTheme.colors.purple else TDTheme.colors.lightPending
    val foreground = if (isSelected) TDTheme.colors.white else TDTheme.colors.onBackground

    val baseModifier = Modifier.clip(RoundedCornerShape(20.dp))
    val elevation = if (isDark) {
        baseModifier.border(
            width = 1.dp,
            color = TDTheme.colors.lightGray.copy(alpha = 0.20f),
            shape = RoundedCornerShape(20.dp),
        )
    } else {
        baseModifier.neumorphicShadow(
            lightShadow = TDTheme.colors.white.copy(alpha = 0.85f),
            darkShadow = TDTheme.colors.lightGray.copy(alpha = 0.30f),
            cornerRadius = 20.dp,
            elevation = 4.dp,
        )
    }

    Row(
        modifier = elevation
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TDText(
            text = stringResource(filterLabelRes(filter)),
            color = foreground,
            style = TDTheme.typography.subheading2,
        )
    }
}

private fun filterLabelRes(filter: HomeFilter): Int = when (filter) {
    HomeFilter.TODAY -> R.string.filter_today
    HomeFilter.DAILY -> R.string.filter_daily
    HomeFilter.WEEKLY -> R.string.filter_weekly
    HomeFilter.MONTHLY -> R.string.filter_monthly
    HomeFilter.YEARLY -> R.string.filter_yearly
}
