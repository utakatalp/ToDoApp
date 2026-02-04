package com.todoapp.mobile.ui.edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.edit.DetailsContract.UiAction
import com.todoapp.mobile.ui.edit.DetailsContract.UiEffect
import com.todoapp.mobile.ui.edit.DetailsContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>(Channel.BUFFERED) }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var originalTask: Task? = null

    init {
        savedStateHandle.get<Long>("taskId")?.let { loadTask(it) }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = taskRepository.getTaskById(taskId) ?: return@launch
            originalTask = task
            _uiState.update {
                it.copy(
                    taskTitle = task.title,
                    taskTimeStart = task.timeStart,
                    taskTimeEnd = task.timeEnd,
                    taskDate = task.date,
                    taskDescription = task.description ?: "",
                    dialogSelectedDate = task.date,
                    isDirty = false,
                )
            }
        }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.OnBackClick -> navigateBack()
            UiAction.OnCancelClick -> cancelChanges()
            UiAction.OnSaveChanges -> saveChanges()
            is UiAction.OnTaskTitleEdit -> updateTitle(uiAction.title)
            is UiAction.OnTaskDescriptionEdit -> updateDescription(uiAction.description)
            is UiAction.OnTaskDateEdit -> updateDate(uiAction.date)
            is UiAction.OnTaskTimeStartEdit -> updateTimeStart(uiAction.time)
            is UiAction.OnTaskTimeEndEdit -> updateTimeEnd(uiAction.time)
            is UiAction.OnDialogDateSelect -> selectDialogDate(uiAction.date)
            UiAction.OnDialogDateDeselect -> deselectDialogDate()
        }
    }

    private fun updateTitle(title: String) {
        updateState {
            it.copy(
                taskTitle = title,
                titleError = null
            )
        }
    }

    private fun updateDescription(description: String) {
        updateState { it.copy(taskDescription = description) }
    }

    private fun updateDate(date: LocalDate) {
        updateState { it.copy(taskDate = date) }
    }

    private fun updateTimeStart(time: LocalTime) {
        updateState { it.copy(taskTimeStart = time) }
    }

    private fun updateTimeEnd(time: LocalTime) {
        updateState { it.copy(taskTimeEnd = time) }
    }

    private fun selectDialogDate(date: LocalDate) {
        updateState {
            it.copy(
                dialogSelectedDate = date,
                taskDate = date
            )
        }
    }

    private fun deselectDialogDate() {
        updateState { it.copy(dialogSelectedDate = null) }
    }

    private fun saveChanges() {
        if (!validateFields()) return

        val current = _uiState.value
        val existingTask = originalTask ?: return
        val updatedTask = buildUpdatedTask(current, existingTask)

        viewModelScope.launch {
            try {
                taskRepository.update(updatedTask)
                onSaveSuccess(updatedTask)
            } catch (e: IOException) {
                Log.e("EditViewModel", "Failed to save changes", e)
                onSaveFailure()
            }
        }
    }

    private fun cancelChanges() {
        val existingTask = originalTask ?: return

        if (!_uiState.value.isDirty) {
            navigateBack()
            return
        }

        updateState {
            it.copy(
                taskTitle = existingTask.title,
                titleError = null,
                taskTimeStart = existingTask.timeStart,
                taskTimeEnd = existingTask.timeEnd,
                taskDate = existingTask.date,
                taskDescription = existingTask.description ?: "",
                dialogSelectedDate = existingTask.date
            )
        }
        _uiEffect.trySend(UiEffect.ShowToast(R.string.changes_cancelled))
    }

    private suspend fun onSaveSuccess(updatedTask: Task) {
        originalTask = updatedTask
        _uiState.update { it.copy(isDirty = false) }
        _uiEffect.send(UiEffect.ShowToast(R.string.changes_saved))
        navigateBack()
    }

    private suspend fun onSaveFailure() {
        _uiEffect.send(UiEffect.ShowToast(R.string.changes_not_saved))
    }

    private fun validateFields(): Boolean {
        val current = _uiState.value

        val titleError = when {
            current.taskTitle.isBlank() -> R.string.error_title_required
            current.taskTitle.length < MIN_TITLE_LENGTH -> R.string.error_title_too_short
            else -> null
        }

        updateState { it.copy(titleError = titleError) }

        return titleError == null
    }

    private fun navigateBack() {
        _navEffect.trySend(NavigationEffect.Back)
    }

    private fun buildUpdatedTask(current: UiState, existingTask: Task): Task {
        return existingTask.copy(
            title = current.taskTitle,
            description = current.taskDescription.ifBlank { null },
            date = current.taskDate,
            timeStart = current.taskTimeStart ?: existingTask.timeStart,
            timeEnd = current.taskTimeEnd ?: existingTask.timeEnd
        )
    }

    private fun computeIsDirty(state: UiState): Boolean {
        val original = originalTask ?: return false
        val candidateTask = original.copy(
            title = state.taskTitle,
            description = state.taskDescription.ifBlank { null },
            date = state.taskDate,
            timeStart = state.taskTimeStart ?: original.timeStart,
            timeEnd = state.taskTimeEnd ?: original.timeEnd
        )
        return candidateTask != original
    }

    private inline fun updateState(block: (UiState) -> UiState) {
        _uiState.update { current ->
            val updated = block(current)
            updated.copy(isDirty = computeIsDirty(updated))
        }
    }

    private companion object {
        const val MIN_TITLE_LENGTH = 3
    }
}
