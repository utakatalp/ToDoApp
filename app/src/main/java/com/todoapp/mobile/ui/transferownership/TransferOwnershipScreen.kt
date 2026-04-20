package com.todoapp.mobile.ui.transferownership

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.todoapp.mobile.ui.groupdetail.MemberAvatar
import com.todoapp.mobile.ui.transferownership.TransferOwnershipContract.TransferMemberUiItem
import com.todoapp.mobile.ui.transferownership.TransferOwnershipContract.UiAction
import com.todoapp.uikit.components.TDButton
import com.todoapp.uikit.components.TDButtonType
import com.todoapp.uikit.components.TDInfoCard
import com.todoapp.uikit.components.TDText
import com.todoapp.uikit.extensions.collectWithLifecycle
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TransferOwnershipScreen(
    viewModel: TransferOwnershipViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.uiEffect.collectWithLifecycle { effect ->
        when (effect) {
            is TransferOwnershipContract.UiEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    TransferOwnershipContent(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun TransferOwnershipContent(
    uiState: TransferOwnershipContract.UiState,
    onAction: (UiAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TDTheme.colors.background),
    ) {
        when (uiState) {
            is TransferOwnershipContract.UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TDTheme.colors.primary)
                }
            }
            is TransferOwnershipContract.UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TDText(text = uiState.message, color = TDTheme.colors.crossRed)
                }
            }
            is TransferOwnershipContract.UiState.Success -> {
                TransferOwnershipSuccessContent(uiState = uiState, onAction = onAction)
            }
        }
    }
}

@Composable
private fun TransferOwnershipSuccessContent(
    uiState: TransferOwnershipContract.UiState.Success,
    onAction: (UiAction) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            TDInfoCard(
                text = stringResource(com.todoapp.mobile.R.string.transfer_ownership_info),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { onAction(UiAction.OnSearchChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    TDText(
                        text = stringResource(com.todoapp.mobile.R.string.search_members),
                        color = TDTheme.colors.gray,
                        style = TDTheme.typography.subheading3,
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TDTheme.colors.primary,
                    unfocusedBorderColor = TDTheme.colors.lightGray,
                    focusedTextColor = TDTheme.colors.onBackground,
                    unfocusedTextColor = TDTheme.colors.onBackground,
                    cursorColor = TDTheme.colors.primary,
                ),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.family_members),
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.gray,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        items(uiState.filteredMembers, key = { it.userId }) { member ->
            TransferMemberRow(
                member = member,
                isSelected = uiState.selectedUserId == member.userId,
                onClick = { onAction(UiAction.OnMemberSelected(member.userId)) },
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            TDButton(
                text = stringResource(com.todoapp.mobile.R.string.transfer_ownership),
                type = TDButtonType.PRIMARY,
                fullWidth = true,
                isEnable = uiState.selectedUserId != null,
                onClick = { onAction(UiAction.OnTransferConfirm) },
            )
            Spacer(modifier = Modifier.height(8.dp))
            TDText(
                text = stringResource(com.todoapp.mobile.R.string.transfer_cannot_undo),
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.gray,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TransferMemberRow(
    member: TransferMemberUiItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TDTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MemberAvatar(
            initials = member.initials,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            TDText(
                text = member.displayName,
                style = TDTheme.typography.subheading2,
                color = TDTheme.colors.onBackground,
            )
            TDText(
                text = member.subtitle,
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.gray,
            )
        }
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = TDTheme.colors.primary),
            modifier = Modifier.size(24.dp),
        )
    }
}
