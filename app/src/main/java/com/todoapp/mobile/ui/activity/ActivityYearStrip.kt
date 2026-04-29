package com.todoapp.mobile.ui.activity

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.todoapp.mobile.R
import com.todoapp.uikit.components.TDYearStrip
import java.time.YearMonth

@Composable
internal fun ActivityYearStrip(
    selectedMonth: YearMonth,
    buckets: List<Pair<YearMonth, Int>>,
    onMonthClick: (YearMonth) -> Unit,
) {
    val locale = currentLocale()
    val labels = remember(buckets, locale) {
        buckets.map { (month, _) -> yearMonthShortLabel(month, locale) }
    }
    val counts = remember(buckets) { buckets.map { it.second } }
    val selectedIndex = remember(buckets, selectedMonth) {
        buckets.indexOfFirst { it.first == selectedMonth }.coerceAtLeast(0)
    }
    TDYearStrip(
        title = stringResource(R.string.activity_year_strip_title),
        monthLabels = labels,
        monthCounts = counts,
        selectedIndex = selectedIndex,
        onMonthClick = { idx -> onMonthClick(buckets[idx].first) },
    )
}
