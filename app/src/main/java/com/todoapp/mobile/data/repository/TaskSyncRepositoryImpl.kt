package com.todoapp.mobile.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.todoapp.mobile.data.worker.FetchTasksWorker
import com.todoapp.mobile.data.worker.SyncWorker
import com.todoapp.mobile.domain.repository.TaskSyncRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TaskSyncRepositoryImpl @Inject constructor(@ApplicationContext context: Context) : TaskSyncRepository {
    private val workManager = WorkManager.getInstance(context)

    override fun syncPendingTasks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val sync = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            SYNC_WORK,
            ExistingWorkPolicy.REPLACE,
            sync
        ).enqueue()
    }

    override fun fetchTasks() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val fetch = OneTimeWorkRequestBuilder<FetchTasksWorker>()
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            FETCH_WORK,
            ExistingWorkPolicy.REPLACE,
            fetch
        ).enqueue()
    }

    companion object {
        const val SYNC_WORK = "sync_work"
        const val FETCH_WORK = "fetch_work"
    }
}
