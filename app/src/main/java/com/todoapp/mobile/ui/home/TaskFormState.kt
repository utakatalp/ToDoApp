package com.todoapp.mobile.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.TaskCategory
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
    @StringRes val titleErrorRes: Int? = null,
    @StringRes val dateErrorRes: Int? = null,
    @StringRes val timeErrorRes: Int? = null,
    val selectedGroupId: Long? = null,
    val selectedPriority: String? = null,
    val selectedAssigneeId: Long? = null,
    val pendingPhotos: List<PendingPhoto> = emptyList(),
    val existingPhotos: List<ExistingPhoto> = emptyList(),
    val photoIdsToDelete: Set<Long> = emptySet(),
    /**
     * Minutes before the task's due time at which to fire the reminder alarm.
     * 0L = on-time, positive = N minutes before, null = no reminder.
     */
    val reminderOffsetMinutes: Long? = 0L,
    val selectedCategory: TaskCategory = TaskCategory.PERSONAL,
    val customCategoryName: String = "",
    val selectedRecurrence: Recurrence = Recurrence.NONE,
)

data class PendingPhoto(
    val bytes: ByteArray,
    val mimeType: String,
)

data class ExistingPhoto(
    val id: Long,
    val url: String,
)

sealed interface TaskFormUiAction {
    data object Dismiss : TaskFormUiAction

    data object Create : TaskFormUiAction

    data class TitleChange(
        val title: String,
    ) : TaskFormUiAction

    data class DateSelect(
        val date: LocalDate,
    ) : TaskFormUiAction

    data object DateDeselect : TaskFormUiAction

    data class TimeStartChange(
        val time: LocalTime,
    ) : TaskFormUiAction

    data class TimeEndChange(
        val time: LocalTime,
    ) : TaskFormUiAction

    data class DescriptionChange(
        val description: String,
    ) : TaskFormUiAction

    data object ToggleAdvancedSettings : TaskFormUiAction

    data class SecretChange(
        val isSecret: Boolean,
    ) : TaskFormUiAction

    data class GroupSelectionChanged(
        val groupId: Long?,
    ) : TaskFormUiAction

    data class PriorityChange(
        val priority: String?,
    ) : TaskFormUiAction

    data class AssigneeChange(
        val userId: Long?,
    ) : TaskFormUiAction

    data class PhotoPicked(
        val bytes: ByteArray,
        val mimeType: String,
    ) : TaskFormUiAction

    data class PhotoRemoveAt(
        val index: Int,
    ) : TaskFormUiAction

    data class ExistingPhotoToggleDelete(
        val photoId: Long,
    ) : TaskFormUiAction

    data class ReminderOffsetChange(
        val minutes: Long?,
    ) : TaskFormUiAction

    data class CategoryChange(
        val category: TaskCategory,
    ) : TaskFormUiAction

    data class CustomCategoryNameChange(
        val name: String,
    ) : TaskFormUiAction

    data class RecurrenceChange(
        val recurrence: Recurrence,
    ) : TaskFormUiAction
}
