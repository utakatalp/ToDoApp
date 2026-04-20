package com.todoapp.mobile.ui.grouptaskdetail

import com.todoapp.mobile.ui.groupdetail.GroupDetailContract
import java.time.LocalDate
import java.time.LocalTime

object GroupTaskDetailContract {
    data class TaskUiModel(
        val id: Long,
        val title: String,
        val description: String?,
        val priority: String?,
        val dueTime: String?,
        val rawDueDate: Long?,
        val isCompleted: Boolean,
        val assigneeName: String?,
        val assigneeInitials: String?,
        val assigneeAvatarUrl: String?,
        val assigneeUserId: Long?,
        val isAssignedToMe: Boolean,
        val canDelete: Boolean,
        val photoUrls: List<String> = emptyList(),
    )

    sealed interface UiState {
        data object Loading : UiState

        data class Success(
            val task: TaskUiModel,
            val groupName: String,
            val members: List<GroupDetailContract.GroupMemberUiItem> = emptyList(),
            val isEditSheetOpen: Boolean = false,
            val editTitle: String = "",
            val editDescription: String = "",
            val editDate: LocalDate? = null,
            val editTime: LocalTime? = null,
            val editAssigneeId: Long? = null,
            val isSaving: Boolean = false,
        ) : UiState

        data class Error(
            val message: String,
        ) : UiState
    }

    sealed interface UiAction {
        data object OnBackTap : UiAction

        data object OnToggleComplete : UiAction

        data object OnEditTap : UiAction

        data object OnEditDismiss : UiAction

        data object OnEditSave : UiAction

        data class OnEditTitleChange(
            val title: String,
        ) : UiAction

        data class OnEditDescriptionChange(
            val description: String,
        ) : UiAction

        data class OnEditDateSelect(
            val date: LocalDate,
        ) : UiAction

        data object OnEditDateDeselect : UiAction

        data class OnEditTimeChange(
            val time: LocalTime,
        ) : UiAction

        data class OnEditAssigneeChange(
            val userId: Long?,
        ) : UiAction

        data class OnPhotoPicked(
            val bytes: ByteArray,
            val mimeType: String,
        ) : UiAction

        data class OnPhotoDelete(
            val photoId: Long,
        ) : UiAction
    }

    sealed interface UiEffect {
        data class ShowToast(
            val message: String,
        ) : UiEffect
    }
}
