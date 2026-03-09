package com.todoapp.mobile.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.domain.repository.personal.PersonalTaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchTasksWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val taskRepository: PersonalTaskRepository
) : CoroutineWorker(ctx, params) {
    companion object {
        private const val TAG = "FetchTasksWorker"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "doWork: started, id=$id, runAttemptCount=$runAttemptCount")

        return taskRepository.syncRemoteTasksToLocal()
            .fold(
                onSuccess = {
                    Log.d(TAG, "doWork: syncRemoteTasksWithLocal SUCCESS")
                    Result.success()
                },
                onFailure = { throwable ->
                    Log.e(TAG, "doWork: syncRemoteTasksWithLocal FAILED", throwable)
                    when (throwable) {
                        is DomainException.NoInternet -> {
                            Log.d(TAG, "doWork: NoInternet -> RETRY")
                            Result.retry()
                        }
                        is DomainException.Server -> {
                            Log.d(TAG, "doWork: Server error -> RETRY")
                            Result.retry()
                        }
                        else -> {
                            Log.d(TAG, "doWork: Unknown error -> FAILURE")
                            Result.failure()
                        }
                    }
                }
            )
    }
}
