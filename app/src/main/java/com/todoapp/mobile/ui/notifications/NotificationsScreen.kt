package com.todoapp.mobile.ui.notifications

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.Notification
import com.todoapp.mobile.domain.model.NotificationType
import com.todoapp.mobile.ui.notifications.NotificationsContract.UiAction
import com.todoapp.mobile.ui.notifications.NotificationsContract.UiEffect
import com.todoapp.mobile.ui.notifications.NotificationsContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonSize
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.modifier.neumorphicShadow
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    uiState: UiState,
    uiEffect: Flow<UiEffect>,
    onAction: (UiAction) -> Unit,
) {
    val context = LocalContext.current
    uiEffect.collectWithLifecycle {
        when (it) {
            is UiEffect.ShowToast -> Toast.makeText(context, it.resId, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background),
    ) {
        if (uiState is UiState.Success && uiState.items.isNotEmpty()) {
            Header(
                hasUnread = uiState.items.any { !it.isRead },
                onMarkAllRead = { onAction(UiAction.OnMarkAllRead) },
            )
        }
        when (uiState) {
            is UiState.Loading -> LoadingView()
            is UiState.Error -> ErrorView(uiState.message) { onAction(UiAction.OnRetry) }
            is UiState.Success -> PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                isRefreshing = uiState.isRefreshing,
                onRefresh = { onAction(UiAction.OnPullToRefresh) },
            ) {
                if (uiState.items.isEmpty()) {
                    EmptyView()
                } else {
                    NotificationsList(
                        items = uiState.items,
                        onItemTap = { onAction(UiAction.OnItemTap(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(hasUnread: Boolean, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TDButton(
            text = stringResource(R.string.notifications_mark_all_read),
            isEnable = hasUnread,
            type = TDButtonType.OUTLINE,
            size = TDButtonSize.SMALL,
            onClick = onMarkAllRead,
        )
    }
}

@Composable
private fun NotificationsList(
    items: List<Notification>,
    onItemTap: (Notification) -> Unit,
) {
    val today = LocalDate.now()
    val zone = ZoneId.systemDefault()
    val (todayItems, earlierItems) = items.partition { item ->
        Instant.ofEpochMilli(item.createdAt).atZone(zone).toLocalDate() == today
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (todayItems.isNotEmpty()) {
            item(key = "section-today") {
                SectionHeader(text = stringResource(R.string.notifications_section_today))
            }
            items(items = todayItems, key = { "today-${it.id}" }) { item ->
                NotificationCard(notification = item, onClick = { onItemTap(item) })
            }
        }
        if (earlierItems.isNotEmpty()) {
            item(key = "section-earlier") {
                SectionHeader(text = stringResource(R.string.notifications_section_earlier))
            }
            items(items = earlierItems, key = { "earlier-${it.id}" }) { item ->
                NotificationCard(notification = item, onClick = { onItemTap(item) })
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    TDText(
        text = text,
        style = TDTheme.typography.subheading2,
        color = TDTheme.colors.pendingGray,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TDTheme.colors.purple)
    }
}

@Composable
private fun EmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TDTheme.colors.bgColorPurple),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(com.example.uikit.R.drawable.ic_notification),
                    contentDescription = null,
                    tint = TDTheme.colors.purple,
                    modifier = Modifier.size(36.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            TDText(
                text = stringResource(R.string.notifications_empty),
                style = TDTheme.typography.heading4,
                color = TDTheme.colors.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            TDText(
                text = stringResource(R.string.notifications_empty_subtitle),
                style = TDTheme.typography.regularTextStyle,
                color = TDTheme.colors.pendingGray,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TDText(
                text = message,
                style = TDTheme.typography.subheading2,
                color = TDTheme.colors.crossRed,
            )
            Spacer(Modifier.height(12.dp))
            TDButton(
                text = stringResource(R.string.retry),
                type = TDButtonType.OUTLINE,
                size = TDButtonSize.SMALL,
                onClick = onRetry,
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val isDark = TDTheme.isDark
    val (iconRes, iconTint, iconBg) = iconConfigFor(notification.type)
    val rendered = NotificationContent.render(
        context = context,
        type = notification.type,
        payload = notification.payload,
        fallbackTitle = notification.title,
        fallbackBody = notification.body,
    )
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
                    darkShadow = TDTheme.colors.darkPending.copy(alpha = 0.15f),
                    cornerRadius = 16.dp,
                    elevation = 6.dp,
                )
            }
        }
        .clip(RoundedCornerShape(16.dp))
        .background(TDTheme.colors.lightPending)
        .clickable(onClick = onClick)
        .padding(14.dp)
    Column(modifier = cardModifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val titleStyle = if (!notification.isRead) {
                    TDTheme.typography.subheading1.copy(fontWeight = FontWeight.Bold)
                } else {
                    TDTheme.typography.subheading1
                }
                TDText(
                    text = rendered.title,
                    style = titleStyle,
                    color = TDTheme.colors.onBackground,
                )
                if (rendered.body.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    TDText(
                        text = rendered.body,
                        style = TDTheme.typography.regularTextStyle,
                        color = TDTheme.colors.gray,
                    )
                }
                Spacer(Modifier.height(4.dp))
                TDText(
                    text = relativeTimeFor(notification.createdAt, context),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.pendingGray,
                )
            }
            if (!notification.isRead) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(TDTheme.colors.purple),
                )
            }
        }

        if (notification.type == NotificationType.INVITATION_RECEIVED) {
            InvitationGroupSummary(payload = notification.payload)
        }
    }
}

@Composable
private fun InvitationGroupSummary(payload: Map<String, String>) {
    val description = payload["groupDescription"]?.takeIf { it.isNotBlank() }
    val memberCount = payload["memberCount"]?.toIntOrNull()
    if (description == null && memberCount == null) return
    Spacer(Modifier.height(12.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TDTheme.colors.bgColorPurple)
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        if (description != null) {
            TDText(
                text = description,
                style = TDTheme.typography.regularTextStyle,
                color = TDTheme.colors.onBackground,
                maxLines = 3,
            )
        }
        if (memberCount != null) {
            if (description != null) Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(com.example.uikit.R.drawable.ic_members),
                    contentDescription = null,
                    tint = TDTheme.colors.purple,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(Modifier.width(6.dp))
                TDText(
                    text = pluralMembers(memberCount, LocalContext.current),
                    style = TDTheme.typography.subheading4,
                    color = TDTheme.colors.darkPurple,
                )
            }
        }
    }
}

private fun pluralMembers(count: Int, context: Context): String = context.resources.getQuantityString(R.plurals.member_count, count, count)

private data class IconConfig(val iconRes: Int, val tint: Color, val background: Color)

@Composable
private fun iconConfigFor(type: NotificationType): IconConfig = when (type) {
    NotificationType.INVITATION_RECEIVED -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_members,
        tint = TDTheme.colors.purple,
        background = TDTheme.colors.bgColorPurple,
    )
    NotificationType.INVITATION_ACCEPTED -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_tasks_done,
        tint = TDTheme.colors.darkGreen,
        background = TDTheme.colors.lightGreen,
    )
    NotificationType.INVITATION_DECLINED -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_error,
        tint = TDTheme.colors.crossRed,
        background = TDTheme.colors.lightRed,
    )
    NotificationType.TASK_ASSIGNED -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_plus,
        tint = TDTheme.colors.darkPending,
        background = TDTheme.colors.lightPending,
    )
    NotificationType.TASK_COMPLETED -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_tasks_done,
        tint = TDTheme.colors.darkGreen,
        background = TDTheme.colors.lightGreen,
    )
    NotificationType.TASK_DUE_SOON -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_sand_clock,
        tint = TDTheme.colors.orange,
        background = TDTheme.colors.lightOrange,
    )
    NotificationType.UNKNOWN -> IconConfig(
        iconRes = com.example.uikit.R.drawable.ic_notification,
        tint = TDTheme.colors.pendingGray,
        background = TDTheme.colors.lightGray.copy(alpha = 0.5f),
    )
}

private fun relativeTimeFor(createdAt: Long, context: Context): String {
    val deltaMs = (System.currentTimeMillis() - createdAt).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(deltaMs)
    val hours = TimeUnit.MILLISECONDS.toHours(deltaMs)
    val days = TimeUnit.MILLISECONDS.toDays(deltaMs)
    return when {
        minutes < 1L -> context.getString(R.string.just_now)
        minutes < 60L -> context.getString(R.string.minutes_ago, minutes.toInt())
        hours < 24L -> context.getString(R.string.hours_ago, hours.toInt())
        days < 2L -> context.getString(R.string.yesterday)
        else -> context.getString(R.string.days_ago, days.toInt())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@TDPreview
@Composable
private fun NotificationsScreenPreview(
    @PreviewParameter(NotificationsPreviewProvider::class) state: UiState,
) {
    TDTheme {
        NotificationsScreen(
            uiState = state,
            uiEffect = emptyFlow(),
            onAction = {},
        )
    }
}
