package com.todoapp.mobile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.domain.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchTasksWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: TaskRepository
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return taskRepository.syncRemoteTasksWithLocal()
            .fold(
                onSuccess = { Result.success() },
                onFailure = { throwable ->
                    when (throwable) {
                        is DomainException.NoInternet,
                        is DomainException.Server -> Result.retry()
                        else -> Result.failure()
                    }
                }
            )
    }
}
