package com.todoapp.mobile.ui.groups.transferownership

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.todoapp.mobile.ui.groups.transferownership.TransferOwnershipContract.TransferMemberUiItem

class TransferOwnershipPreviewProvider : PreviewParameterProvider<TransferOwnershipContract.UiState> {
    override val values: Sequence<TransferOwnershipContract.UiState>
        get() {
            val members =
                listOf(
                    TransferMemberUiItem(1, "Berat Baran", "berat@example.com", null, "BB"),
                    TransferMemberUiItem(2, "Ayse Y.", "ayse@example.com", null, "AY"),
                    TransferMemberUiItem(3, "Mehmet K.", "mehmet@example.com", null, "MK"),
                    TransferMemberUiItem(4, "Fatma D.", "fatma@example.com", null, "FD"),
                )

            return sequenceOf(
                TransferOwnershipContract.UiState.Loading,
                TransferOwnershipContract.UiState.Error("Failed to load members"),
                TransferOwnershipContract.UiState.Success(
                    members = emptyList(),
                    filteredMembers = emptyList(),
                    searchQuery = "",
                    selectedUserId = null,
                ),
                TransferOwnershipContract.UiState.Success(
                    members = members,
                    filteredMembers = members,
                    searchQuery = "",
                    selectedUserId = null,
                ),
                TransferOwnershipContract.UiState.Success(
                    members = members,
                    filteredMembers = members,
                    searchQuery = "",
                    selectedUserId = 2L,
                ),
                TransferOwnershipContract.UiState.Success(
                    members = members,
                    filteredMembers = members.filter { it.displayName.contains("a", true) },
                    searchQuery = "a",
                    selectedUserId = null,
                ),
            )
        }
}
