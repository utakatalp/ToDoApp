package com.todoapp.mobile.ui.home

import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.LocalTime

@Stable
data class TaskFormState(
    val taskTitle: String = "",
    val dialogSelectedDate: LocalDate? = null,
    val taskTimeStart: LocalTime? = null,
    val taskTimeEnd: LocalTime? = null,
    val taskDescription: String = "",
    val isAdvancedSettingsExpanded: Boolean = false,
    val isTaskSecret: Boolean = false,
    val isTitleError: Boolean = false,
    val isTimeError: Boolean = false,
    val isDateError: Boolean = false,
    val selectedGroupId: Long? = null,
    val selectedPriority: String? = null,
    val selectedAssigneeId: Long? = null,
    val pendingPhotos: List<PendingPhoto> = emptyList(),
)

data class PendingPhoto(
    val bytes: ByteArray,
    val mimeType: String,
)

sealed interface TaskFormUiAction {
    data object Dismiss : TaskFormUiAction
    data object Create : TaskFormUiAction
    data class TitleChange(val title: String) : TaskFormUiAction
    data class DateSelect(val date: LocalDate) : TaskFormUiAction
    data object DateDeselect : TaskFormUiAction
    data class TimeStartChange(val time: LocalTime) : TaskFormUiAction
    data class TimeEndChange(val time: LocalTime) : TaskFormUiAction
    data class DescriptionChange(val description: String) : TaskFormUiAction
    data object ToggleAdvancedSettings : TaskFormUiAction
    data class SecretChange(val isSecret: Boolean) : TaskFormUiAction
    data class GroupSelectionChanged(val groupId: Long?) : TaskFormUiAction
    data class PriorityChange(val priority: String?) : TaskFormUiAction
    data class AssigneeChange(val userId: Long?) : TaskFormUiAction
    data class PhotoPicked(val bytes: ByteArray, val mimeType: String) : TaskFormUiAction
    data class PhotoRemoveAt(val index: Int) : TaskFormUiAction
}
