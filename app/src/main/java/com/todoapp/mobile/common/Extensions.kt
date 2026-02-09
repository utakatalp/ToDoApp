package com.todoapp.mobile.common

import com.todoapp.mobile.data.model.network.response.BaseResponse
import com.todoapp.mobile.data.model.network.response.ErrorResponse
import kotlinx.serialization.json.Json
import retrofit2.Response

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
