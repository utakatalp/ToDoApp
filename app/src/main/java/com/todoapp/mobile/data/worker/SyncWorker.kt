package com.todoapp.mobile.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return taskRepository.syncLocalTasksToServer()
            .fold(
                onSuccess = { Result.success() },
                onFailure = { throwable ->
                    Log.e("SyncWorker", "Failed to sync tasks $throwable", throwable)
                    when (throwable) {
                        is DomainException.NoInternet,
                        is DomainException.Server -> {
                            if (runAttemptCount <= MAX_ATTEMPT) {
                                Result.retry()
                            } else {
                                Result.failure()
                            }
                        }
                        is DomainException.Unauthorized -> Result.failure()
                        else -> Result.failure()
                    }
                }
            )
    }
}

const val MAX_ATTEMPT = 2
