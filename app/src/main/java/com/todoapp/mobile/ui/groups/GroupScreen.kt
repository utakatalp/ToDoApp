package com.todoapp.mobile.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.mobile.ui.groups.GroupsContract.UiAction
import com.todoapp.mobile.ui.groups.GroupsContract.UiState
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.theme.TDTheme

@Composable
fun GroupScreen(
    uiState: UiState,
    onAction: (UiAction) -> Unit
) {
    when (uiState) {
        UiState.Empty -> GroupEmptyContent(onCreateNewGroup = { onAction(UiAction.OnCreateNewGroupTap) })
        is UiState.Error -> {}
        UiState.Loading -> {}
        is UiState.Success -> {}
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupEmptyContent(
    onCreateNewGroup: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TDTheme.colors.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Icon(
            painterResource(R.drawable.ic_avatar_new_group),
            contentDescription = "New Group",
            modifier = Modifier.size(192.dp),
            tint = TDTheme.colors.primary.copy(0.81f)
        )
        Spacer(Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TDText(text = "You don't have any family", style = TDTheme.typography.heading2)
            TDText(text = "groups yet.", style = TDTheme.typography.heading2)
        }
        Spacer(Modifier.height(12.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TDText(
                text = "Create a group to start collaborating on",
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
            TDText(
                text = "tasks with your family.",
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
        }
        Spacer(Modifier.height(32.dp))
        TDButton(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            text = "+ Create New Group",
            fullWidth = true
        ) { onCreateNewGroup() }

        Spacer(Modifier.weight(1f))
    }
}
