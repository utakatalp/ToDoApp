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
import timber.log.Timber
import javax.inject.Inject

class TaskSyncRepositoryImpl
@Inject
constructor(
    @ApplicationContext context: Context,
) : TaskSyncRepository {
    private val workManager = WorkManager.getInstance(context)

    @Volatile private var lastFetchAt: Long = 0L

    override fun syncPendingTasks() {
        val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val sync =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

        workManager
            .beginUniqueWork(
                SYNC_WORK,
                ExistingWorkPolicy.REPLACE,
                sync,
            ).enqueue()
    }

    override fun resetCooldown() {
        lastFetchAt = 0L
    }

    override fun fetchTasks(force: Boolean) {
        val withinCooldown = System.currentTimeMillis() - lastFetchAt < FETCH_COOLDOWN_MS
        Timber.tag("TaskFetch").d("fetchTasks(force=$force) withinCooldown=$withinCooldown")
        if (!force && withinCooldown) return
        lastFetchAt = System.currentTimeMillis()

        val constraints =
            Constraints
                .Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val sync =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

        val fetch =
            OneTimeWorkRequestBuilder<FetchTasksWorker>()
                .setConstraints(constraints)
                .build()

        workManager
            .beginUniqueWork(
                FETCH_WORK,
                ExistingWorkPolicy.REPLACE,
                sync,
            ).then(fetch)
            .enqueue()
    }

    companion object {
        const val SYNC_WORK = "sync_work"
        const val FETCH_WORK = "fetch_work"
        private const val FETCH_COOLDOWN_MS = 60_000L
    }
}
