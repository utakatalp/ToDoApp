package com.todoapp.mobile.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.todoapp.mobile.data.model.network.response.BaseResponse
import com.todoapp.mobile.data.model.network.response.ErrorResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
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
            val message = errorBody
                ?.let {
                    runCatching { Json.decodeFromString<ErrorResponse>(it).message }.getOrNull()
                }
                ?: response.message()
                ?: "Something went wrong"

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
