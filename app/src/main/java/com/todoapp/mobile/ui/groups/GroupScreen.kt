package com.todoapp.mobile.ui.groups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
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
        is UiState.Success -> {
            GroupsContent(uiState, onAction)
        }
    }
}

@Composable
private fun GroupsContent(
    uiState: UiState.Success,
    onAction: (UiAction) -> Unit
) {
    Column {
        uiState.groups.forEach {
            TDText(text = it.toString())
            TDButton(text = "Delete this group") {
                onAction(UiAction.OnDeleteGroupTap(it.id))
            }
        }
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painterResource(R.drawable.ic_avatar_new_group),
            contentDescription = stringResource(com.todoapp.mobile.R.string.new_group),
            modifier = Modifier.size(192.dp),
            tint = TDTheme.colors.primary.copy(0.81f)
        )
        Spacer(Modifier.height(32.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.you_don_t_have_any),
                style = TDTheme.typography.heading2
            )
            TDText(text = stringResource(com.todoapp.mobile.R.string.groups_yet), style = TDTheme.typography.heading2)
        }
        Spacer(Modifier.height(12.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.create_a_group_to_start_collaborating_on),
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.tasks_with_your_family),
                style = TDTheme.typography.subheading3,
                color = TDTheme.colors.lightGray
            )
        }
        Spacer(Modifier.height(32.dp))
        TDButton(
            modifier = Modifier.clip(RoundedCornerShape(12.dp)),
            text = stringResource(com.todoapp.mobile.R.string.create_new_group),
            fullWidth = true
        ) { onCreateNewGroup() }
    }
}
