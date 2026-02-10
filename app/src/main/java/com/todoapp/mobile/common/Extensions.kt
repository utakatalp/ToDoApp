package com.todoapp.mobile.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.todoapp.mobile.data.model.network.response.BaseResponse
import com.todoapp.mobile.data.model.network.response.ErrorResponse
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.todoapp.mobile.ui.pomodoro.ModeColorKey
import com.todoapp.mobile.ui.pomodoro.PomodoroModeUi
import com.todoapp.mobile.ui.pomodoro.PomodoroModeUiPreset
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import retrofit2.Response

@Composable
fun <T> Flow<T>.CollectWithLifecycle(collect: suspend (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(this, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            this@CollectWithLifecycle.collect { effect ->
                collect(effect)
            }
        }
    }
}

fun <T> MutableList<T>.move(fromIndex: Int, toIndex: Int) {
    if (fromIndex == toIndex) return
    val item = removeAt(fromIndex)
    add(toIndex, item)
}

fun String.maskTitle(): String {
    return this.first() + "*".repeat(this.length - 1)
}

suspend fun <T> handleRequest(request: suspend () -> Response<BaseResponse<T?>>): Result<T> {
    val response = request()
    if (!response.isSuccessful) {
        val errorMessage = response.errorBody()
            ?.string()
            ?.let { body ->
                runCatching {
                    Json.decodeFromString<ErrorResponse>(body).message
                }.getOrNull()
            }
            ?: "Something went wrong"

        return Result.failure(Exception(errorMessage))
    }
    val body = response.body()
    val data = body?.data
    data?.let {
        return Result.success(it)
    }
    return Result.failure(Exception("Something went wrong"))
}
@Composable
fun PomodoroModeUi.resolveTextColor(): Color {
    return when (colorKey) {
        ModeColorKey.Focus -> TDTheme.colors.primary
        ModeColorKey.ShortBreak -> TDTheme.colors.softPink
        ModeColorKey.LongBreak -> TDTheme.colors.green
        ModeColorKey.OverTime -> TDTheme.colors.red
    }
}

fun PomodoroMode.toUiMode(): PomodoroModeUi = when (this) {
    PomodoroMode.Focus -> PomodoroModeUiPreset.Focus.value
    PomodoroMode.ShortBreak -> PomodoroModeUiPreset.ShortBreak.value
    PomodoroMode.LongBreak -> PomodoroModeUiPreset.LongBreak.value
    PomodoroMode.OverTime -> PomodoroModeUiPreset.OverTime.value
}

fun <T> ArrayDeque<T>.pollFirst(): T? =
    if (isEmpty()) null else removeFirst()
