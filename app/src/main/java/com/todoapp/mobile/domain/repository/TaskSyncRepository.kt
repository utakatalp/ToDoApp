package com.todoapp.mobile.domain.repository

interface TaskSyncRepository {
    fun syncPendingTasks()

    fun fetchTasks(force: Boolean = false)

    fun resetCooldown()
}
