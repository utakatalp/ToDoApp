package com.todoapp.mobile.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.todoapp.mobile.data.model.network.response.BaseResponse
import com.todoapp.mobile.data.model.network.response.ErrorResponse
import com.todoapp.mobile.domain.engine.PomodoroMode
import com.todoapp.mobile.ui.pomodoro.ModeColorKey
import com.todoapp.mobile.ui.pomodoro.PomodoroModeUi
import com.todoapp.mobile.ui.pomodoro.PomodoroModeUiPreset
import com.todoapp.uikit.theme.TDTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

suspend fun <T> handleRequest(
    request: suspend () -> Response<BaseResponse<T?>>
): Result<T> {
    return try {
        val response = request()

        if (response.isSuccessful.not()) {
            val errorBody = response.errorBody()?.string()
            Log.d("error", errorBody.toString())
            val message = errorBody
                ?.let {
                    runCatching { Json.decodeFromString<ErrorResponse>(it).message }.getOrNull()
                }
                ?: response.message()
                ?: "Something went wrong"
                Log.d("error", message)
            return Result.failure(DomainException.Server(message))
        }

        val body = response.body()
            ?: return Result.failure(DomainException.Server("Empty response"))

        val data = body.data
            ?: return Result.failure(DomainException.Server(body.message))

        Result.success(data)
    } catch (t: Throwable) {
        if (t is CancellationException) throw t
        Result.failure(DomainException.fromThrowable(t))
    }
}

sealed class DomainException(message: String) : Exception(message) {

    class NoInternet : DomainException("No internet connection")

    class Unauthorized : DomainException("Unauthorized")

    class Server(message: String) : DomainException(message)

    class Unknown(cause: Throwable) : DomainException(cause.message ?: "Unknown error")

    companion object {
        private const val HTTP_STATUS_UNAUTHORIZED = 401

        fun fromThrowable(t: Throwable): DomainException = when (t) {
            is UnknownHostException,
            is SocketTimeoutException -> NoInternet()
            is HttpException -> {
                if (t.code() == HTTP_STATUS_UNAUTHORIZED) {
                    Unauthorized()
                } else {
                    Server("Server error")
                }
            }
            else -> Unknown(t)
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
suspend fun loginWithFacebook(
    activity: FragmentActivity
): Result<String> = suspendCancellableCoroutine { cont ->

    val callbackManager = CallbackManager.Factory.create()

    val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

    fun resumeOnce(result: Result<String>) {
        cont.tryResume(result)?.let(cont::completeResume)
    }

    if (!hasInternet) {
        resumeOnce(Result.failure(Exception("No internet connection")))
        return@suspendCancellableCoroutine
    }

    LoginManager.getInstance().registerCallback(
        callbackManager,
        object : FacebookCallback<LoginResult> {

            override fun onCancel() {
                resumeOnce(Result.failure(Exception("User cancelled")))
            }

            override fun onError(error: FacebookException) {
                resumeOnce(Result.failure(error))
            }

            override fun onSuccess(result: LoginResult) {
                resumeOnce(Result.success(result.accessToken.token))
            }
        }
    )

    LoginManager.getInstance().logInWithReadPermissions(
        activity,
        callbackManager,
        listOf("public_profile", "email")
    )

    cont.invokeOnCancellation {
        LoginManager.getInstance().unregisterCallback(callbackManager)
    }
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
