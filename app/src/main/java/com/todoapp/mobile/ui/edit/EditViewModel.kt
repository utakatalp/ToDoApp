package com.todoapp.mobile.ui.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.repository.TaskRepository
import com.todoapp.mobile.ui.edit.EditContract.UiAction
import com.todoapp.mobile.ui.edit.EditContract.UiEffect
import com.todoapp.mobile.ui.edit.EditContract.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<UiEffect>(Channel.BUFFERED) }
    val uiEffect: Flow<UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    private fun computeIsDirty(state: UiState): Boolean {
        val original = state.task ?: return false
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

    init {
        savedStateHandle.get<Long>("taskId")?.let { loadTask(it) }
    }

    fun onAction(uiAction: UiAction) {
        when (uiAction) {
            UiAction.OnBackClick -> goToHome()
            UiAction.OnDialogDateDeselect -> dialogDateDeselect()
            is UiAction.OnDialogDateSelect -> dialogDateSelect(uiAction)
            is UiAction.OnTaskDateEdit -> taskDateEdit(uiAction)
            is UiAction.OnTaskDescriptionEdit -> taskDescriptionEdit(uiAction)
            is UiAction.OnTaskTimeStartEdit -> taskTimeStartEdit(uiAction)
            is UiAction.OnTaskTimeEndEdit -> taskTimeEndEdit(uiAction)
            is UiAction.OnTaskTitleEdit -> taskTitleEdit(uiAction)
            UiAction.OnSaveChanges -> saveChanges()
            UiAction.OnCancelClick -> cancelChanges()
        }
    }

    private fun goToHome() {
        _uiEffect.trySend(UiEffect.NavigateBack)
    }

    private fun taskTitleEdit(uiAction: UiAction.OnTaskTitleEdit) {
        updateState { it.copy(taskTitle = uiAction.title) }
    }

    private fun saveChanges() {
        val current = getCurrentState()
        val existingTask = current.task ?: return

        val updatedTask = buildUpdatedTask(current, existingTask)

        viewModelScope.launch {
            val result = updateTask(updatedTask)
            result.fold(
                onSuccess = { handleSaveSuccess(updatedTask) },
                onFailure = { handleSaveFailure() }
            )
        }
    }

    private fun getCurrentState(): UiState = _uiState.value

    private fun buildUpdatedTask(current: UiState, existingTask: Task): Task {
        return existingTask.copy(
            title = current.taskTitle,
            description = current.taskDescription.ifBlank { null },
            date = current.taskDate,
            timeStart = current.taskTimeStart ?: existingTask.timeStart,
            timeEnd = current.taskTimeEnd ?: existingTask.timeEnd
        )
    }

    private suspend fun updateTask(updatedTask: Task): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching { taskRepository.update(updatedTask) }
        }
    }

    private suspend fun handleSaveSuccess(updatedTask: Task) {
        _uiState.update { it.copy(task = updatedTask, isDirty = false) }
        _uiEffect.send(UiEffect.ShowToast(R.string.save_changes))
        _uiEffect.send(UiEffect.NavigateBack)
    }

    private suspend fun handleSaveFailure() {
        val message = R.string.changes_not_saved
        _uiEffect.send(UiEffect.ShowToast(message))
    }

    private fun cancelChanges() {
        val current = _uiState.value
        val existingTask = current.task ?: return
        updateState {
            it.copy(
                taskTitle = existingTask.title,
                taskTimeStart = existingTask.timeStart,
                taskTimeEnd = existingTask.timeEnd,
                taskDate = existingTask.date,
                taskDescription = existingTask.description ?: "",
                dialogSelectedDate = existingTask.date
            )
        }
        _uiEffect.trySend(UiEffect.ShowToast(R.string.changes_cancelled))
    }

    private fun taskDescriptionEdit(uiAction: UiAction.OnTaskDescriptionEdit) {
        updateState { it.copy(taskDescription = uiAction.description) }
    }

    private fun taskTimeStartEdit(uiAction: UiAction.OnTaskTimeStartEdit) {
        updateState { it.copy(taskTimeStart = uiAction.time) }
    }

    private fun taskTimeEndEdit(uiAction: UiAction.OnTaskTimeEndEdit) {
        updateState { it.copy(taskTimeEnd = uiAction.time) }
    }

    private fun taskDateEdit(uiAction: UiAction.OnTaskDateEdit) {
        updateState { it.copy(taskDate = uiAction.date) }
    }

    private fun dialogDateSelect(uiAction: UiAction.OnDialogDateSelect) {
        updateState { it.copy(dialogSelectedDate = uiAction.date) }
    }

    private fun dialogDateDeselect() {
        updateState { it.copy(dialogSelectedDate = null) }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            val task = withContext(Dispatchers.IO) {
                taskRepository.getTaskById(taskId)
            }
            _uiState.update { current ->
                if (task == null) return@update current

                current.copy(
                    task = task,
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
}
