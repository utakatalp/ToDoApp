package com.todoapp.mobile.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.todoapp.mobile.data.worker.RescheduleAlarmsWorker
import timber.log.Timber

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action !in HANDLED_ACTIONS) return
        Timber.tag(TAG).d("Boot/replace event received: %s", action)

        val request =
            OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>()
                .apply {
                    runCatching {
                        setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    }
                }.build()

        WorkManager
            .getInstance(context.applicationContext)
            .enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    private companion object {
        const val TAG = "BootReceiver"
        const val UNIQUE_NAME = "reschedule_alarms_after_boot"
        val HANDLED_ACTIONS =
            setOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_PACKAGE_REPLACED,
            )
    }
}
