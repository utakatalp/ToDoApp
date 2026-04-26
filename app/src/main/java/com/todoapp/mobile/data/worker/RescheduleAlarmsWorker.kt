package com.todoapp.mobile.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.todoapp.mobile.domain.alarm.RescheduleAllAlarmsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

@HiltWorker
class RescheduleAlarmsWorker
@AssistedInject
constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val rescheduleAllAlarms: RescheduleAllAlarmsUseCase,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = runCatching { rescheduleAllAlarms() }
        .fold(
            onSuccess = { Result.success() },
            onFailure = {
                Timber.tag(TAG).w(it, "reschedule failed")
                Result.retry()
            },
        )

    private companion object {
        const val TAG = "RescheduleAlarmsWorker"
    }
}
