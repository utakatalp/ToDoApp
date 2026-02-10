package com.todoapp.mobile.ui.edit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.navigation.NavigationEffect
import com.todoapp.mobile.ui.edit.EditContract.UiAction
import com.todoapp.mobile.ui.edit.EditContract.UiEffect
import com.todoapp.mobile.ui.edit.EditContract.UiState
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
class EditViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>(Channel.BUFFERED) }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private val _navEffect by lazy { Channel<NavigationEffect>() }
    val navEffect by lazy { _navEffect.receiveAsFlow() }

    private var originalTask: Task? = null
    private var currentTaskId: Long? = null

    fun loadTask(taskId: Long) {
        currentTaskId = taskId
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                val task = taskRepository.getTaskById(taskId)
                if (task == null) {
                    _uiState.value = UiState.Error(message = R.string.error_task_not_found.toString())
                    return@launch
                }
                originalTask = task
                _uiState.value = UiState.Success(
                    taskTitle = task.title,
                    taskTimeStart = task.timeStart,
                    taskTimeEnd = task.timeEnd,
                    taskDate = task.date,
                    taskDescription = task.description ?: "",
                    dialogSelectedDate = task.date,
                    isDirty = false,
                    titleError = null,
                    isSaving = false
                )
            } catch (e: IOException) {
                _uiState.value = UiState.Error(message = "Failed to load task", throwable = e)
                Log.e("EditViewModel", "Failed to load task", e)
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
            UiAction.OnRetry -> retry()
        }
    }

    private fun retry() {
        currentTaskId?.let { loadTask(it) }
    }

    private inline fun updateSuccessState(crossinline transform: (UiState.Success) -> UiState.Success) {
        _uiState.update { currentState ->
            when (currentState) {
                is UiState.Success -> {
                    val updated = transform(currentState)
                    updated.copy(isDirty = computeIsDirty(updated))
                }

                else -> currentState
            }
        }
    }

    private fun updateTitle(title: String) {
        updateSuccessState {
            it.copy(taskTitle = title, titleError = null)
        }
    }

    private fun updateDescription(description: String) {
        updateSuccessState { it.copy(taskDescription = description) }
    }

    private fun updateDate(date: LocalDate) {
        updateSuccessState { it.copy(taskDate = date) }
    }

    private fun updateTimeStart(time: LocalTime) {
        updateSuccessState { it.copy(taskTimeStart = time) }
    }

    private fun updateTimeEnd(time: LocalTime) {
        updateSuccessState { it.copy(taskTimeEnd = time) }
    }

    private fun selectDialogDate(date: LocalDate) {
        updateSuccessState {
            it.copy(dialogSelectedDate = date, taskDate = date)
        }
    }

    private fun deselectDialogDate() {
        updateSuccessState { it.copy(dialogSelectedDate = null) }
    }

    private fun saveChanges() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return
        if (currentState.isSaving) return
        if (!validateFields(currentState)) return

        val existingTask = originalTask ?: return
        val updatedTask = buildUpdatedTask(currentState, existingTask)

        updateSuccessState { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                taskRepository.update(updatedTask)
                onSaveSuccess(updatedTask)
            } catch (e: IOException) {
                Log.e("EditViewModel", "Failed to save changes", e)
                updateSuccessState { it.copy(isSaving = false) }
                onSaveFailure()
            }
        }
    }

    private fun cancelChanges() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success) return

        val existingTask = originalTask ?: return

        if (!currentState.isDirty) {
            navigateBack()
            return
        }

        _uiState.value = UiState.Success(
            taskTitle = existingTask.title,
            titleError = null,
            taskTimeStart = existingTask.timeStart,
            taskTimeEnd = existingTask.timeEnd,
            taskDate = existingTask.date,
            taskDescription = existingTask.description ?: "",
            dialogSelectedDate = existingTask.date,
            isDirty = false,
            isSaving = true
        )
        _uiEffect.trySend(UiEffect.ShowToast(R.string.changes_cancelled))
    }

    private suspend fun onSaveSuccess(updatedTask: Task) {
        originalTask = updatedTask
        updateSuccessState { it.copy(isDirty = false) }
        _uiEffect.send(UiEffect.ShowToast(R.string.changes_saved))
        navigateBack()
    }

    private suspend fun onSaveFailure() {
        _uiEffect.send(UiEffect.ShowToast(R.string.changes_not_saved))
    }

    private fun validateFields(state: UiState.Success): Boolean {
        val titleError = when {
            state.taskTitle.isBlank() -> R.string.error_title_required
            state.taskTitle.length < MIN_TITLE_LENGTH -> R.string.error_title_too_short
            else -> null
        }

        if (titleError != null) {
            updateSuccessState { it.copy(titleError = titleError) }
            return false
        }
        return true
    }

    private fun navigateBack() {
        _navEffect.trySend(NavigationEffect.Back)
    }

    private fun buildUpdatedTask(current: UiState.Success, existingTask: Task): Task {
        return existingTask.copy(
            title = current.taskTitle,
            description = current.taskDescription.ifBlank { null },
            date = current.taskDate,
            timeStart = current.taskTimeStart ?: existingTask.timeStart,
            timeEnd = current.taskTimeEnd ?: existingTask.timeEnd
        )
    }

    private fun computeIsDirty(state: UiState.Success): Boolean {
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

    private companion object {
        const val MIN_TITLE_LENGTH = 3
    }
}
