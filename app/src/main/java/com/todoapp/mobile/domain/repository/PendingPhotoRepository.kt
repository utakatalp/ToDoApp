package com.todoapp.mobile.domain.repository

interface PendingPhotoRepository {
    suspend fun queue(localTaskId: Long, bytes: ByteArray, mimeType: String): Result<Unit>

    suspend fun drain(localTaskId: Long, remoteTaskId: Long, upload: suspend (ByteArray, String) -> Result<Unit>): Int

    suspend fun clearAll()
}
