package com.todoapp.mobile.data.repository.personal

import android.util.Log
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toAlarmItem
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.mapper.toPersonalEntity
import com.todoapp.mobile.data.mapper.toRequest
import com.todoapp.mobile.data.mapper.toTaskPersonal
import com.todoapp.mobile.data.mapper.toUpdateRequest
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.personal.PersonalTaskEntity
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.source.local.datasource.personal.PersonalTaskLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.personal.PersonalTaskRemoteDataSource
import com.todoapp.mobile.domain.alarm.AlarmScheduler
import com.todoapp.mobile.domain.alarm.AlarmType
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.observer.ConnectivityObserver
import com.todoapp.mobile.domain.repository.personal.PersonalTaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class PersonalTaskRepositoryImpl @Inject constructor(
    private val remote: PersonalTaskRemoteDataSource,
    private val local: PersonalTaskLocalDataSource,
    private val alarmScheduler: AlarmScheduler,
    connectivityObserver: ConnectivityObserver,
) : PersonalTaskRepository {

    private val isInternetConnected = connectivityObserver.isConnected

    override fun observeTasks(): Flow<List<Task.Personal>> {
        return local.observeAll().map { entities ->
            entities.map { it.toTaskPersonal() }
        }
    }

    override fun observeTasksByDate(dateEpochDay: LocalDate): Flow<List<Task.Personal>> {
        return local.observeByDate(date = dateEpochDay.toEpochDay()).map { entities ->
            entities.map { it.toTaskPersonal() }
        }
    }

    override fun observeRange(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate,
    ): Flow<List<Task.Personal>> {
        return local.observeRange(
            startDate = startDateEpochDay.toEpochDay(),
            endDate = endDateEpochDay.toEpochDay(),
        ).map { entities ->
            entities.map { it.toTaskPersonal() }
        }
    }

    override fun observeCompletedTaskCount(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate
    ): Flow<Int> {
        return local.countInRange(startDateEpochDay.toEpochDay(), endDateEpochDay.toEpochDay(), isCompleted = true)
    }

    override fun observeNonCompletedTaskCount(
        startDateEpochDay: LocalDate,
        endDateEpochDay: LocalDate
    ): Flow<Int> {
        return local.countInRange(startDateEpochDay.toEpochDay(), endDateEpochDay.toEpochDay(), isCompleted = false)
    }

    override fun observePendingTasksInAWeek(date: LocalDate): Flow<Int> {
        return observeTaskCountInWeek(date = date, isCompleted = false)
    }

    override fun observeCompletedTasksInAWeek(date: LocalDate): Flow<Int> {
        return observeTaskCountInWeek(date = date, isCompleted = true)
    }

    override fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())

        return local.observeCompletedCountsByDay(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
        ).map { dayCounts ->
            // dayCounts is sparse (only days that have completed tasks). Convert to dense 7-day list (Mon..Sun).
            val map = dayCounts.associate { LocalDate.ofEpochDay(it.date) to it.count }

            (0 until DAYS_IN_WEEK).map { dayOffset ->
                map[weekStart.plusDays(dayOffset.toLong())] ?: 0
            }
        }
    }

    private fun observeTaskCountInWeek(
        date: LocalDate,
        isCompleted: Boolean,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return local.countInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = isCompleted,
        )
    }

    override fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int> {
        return observeTasksYearToDate(date, isCompleted = true)
    }

    override fun observePendingTasksYearToDate(date: LocalDate): Flow<Int> {
        return observeTasksYearToDate(date, isCompleted = false)
    }

    override suspend fun createTask(taskRequest: TaskRequest): Result<Unit> = withContext(Dispatchers.IO) {
        val orderIndex = nextOrderForDate(LocalDate.ofEpochDay(taskRequest.date))
        // if no internet connection, save locally
        if (!(isInternetConnected.value)) {
            val taskEntity = taskRequest.toPersonalEntity(orderIndex)
            local.insert(taskEntity)
            scheduleTaskReminders(taskEntity)

            return@withContext Result.failure(DomainException.NoInternet())
        }

        runCatching {
            remote.createTask(taskRequest).fold(
                // if success, save remotely and locally
                onSuccess = { remoteTask ->
                    val taskEntity = remoteTask.toEntity(orderIndex = orderIndex)
                    local.insert(taskEntity)
                    scheduleTaskReminders(taskEntity)
                },
                // if server is down save locally
                onFailure = { t ->
                    val taskEntity = taskRequest.toPersonalEntity(orderIndex)
                    local.insert(taskEntity)
                    scheduleTaskReminders(taskEntity)
                    throw t
                },
            )
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun updateTask(task: Task.Personal): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val existing = local.getTaskById(task.id)
                ?: throw DomainException.fromThrowable(
                    IllegalStateException("updateTask: local task not found id=${task.id}")
                )

            // Preserve the current orderIndex if caller did not set it properly
            val normalized = task.copy(orderIndex = task.orderIndex.takeIf { it != 0 } ?: existing.orderIndex)

            // If it's not synced yet (pending create/update/delete), we avoid remote calls.
            if (existing.syncStatus != SyncStatus.SYNCED) {
                val keepStatus = if (existing.syncStatus == SyncStatus.PENDING_CREATE) {
                    SyncStatus.PENDING_CREATE
                } else {
                    // If it was pending delete, keep it pending delete
                    existing.syncStatus
                }

                local.update(
                    normalized.toEntity(orderIndex = normalized.orderIndex).copy(
                        id = existing.id,
                        remoteId = existing.remoteId,
                        syncStatus = keepStatus,
                    )
                )
                return@runCatching
            }

            // Synced -> try remote update
            val remoteId = existing.remoteId

            if (!(isInternetConnected.value)) {
                val updated = normalized.toEntity(orderIndex = existing.orderIndex).copy(
                    id = existing.id,
                    remoteId = existing.remoteId,
                    syncStatus = SyncStatus.PENDING_UPDATE,
                )
                local.update(updated)
                return@runCatching
            }

            remote.updateTask(normalized.toUpdateRequest(remoteId!!)).fold(

                onSuccess = { remoteTask ->
                    // Persist remote representation locally
                    local.update(remoteTask.toEntity(orderIndex = existing.orderIndex))
                },
                onFailure = { t ->
                    // Mark local as pending update (offline-first)
                    val updated = normalized.toEntity(orderIndex = existing.orderIndex).copy(
                        id = existing.id,
                        remoteId = existing.remoteId,
                        syncStatus = SyncStatus.PENDING_UPDATE,
                    )
                    local.update(updated)
                    throw t
                },
            )
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun deleteTask(task: Task.Personal): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val existing = local.getTaskById(task.id) ?: return@runCatching

            // If it never reached server (or still pending create), delete locally.
            if (existing.syncStatus != SyncStatus.SYNCED || existing.remoteId == null) {
                local.delete(existing)
                return@runCatching
            }

            val remoteId = existing.remoteId

            if (!(isInternetConnected.value)) {
                local.update(existing.copy(syncStatus = SyncStatus.PENDING_DELETE))
                return@runCatching
            }

            remote.deleteTask(remoteId).fold(
                onSuccess = {
                    local.delete(existing)
                },
                onFailure = { t ->
                    local.update(existing.copy(syncStatus = SyncStatus.PENDING_DELETE))
                    throw t
                },
            )
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun updateTaskCompletion(
        id: Long,
        isCompleted: Boolean,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val before = local.getTaskById(id) ?: return@runCatching

            local.updateTaskCompletion(id = id, isCompleted = isCompleted)

            // If it was synced, changing completion means we need to sync an update.
            if (before.syncStatus == SyncStatus.SYNCED) {
                local.updateSyncStatus(id = id, status = SyncStatus.PENDING_UPDATE)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun getTaskById(id: Long): Task.Personal? = withContext(Dispatchers.IO) {
        local.getTaskById(id)?.toTaskPersonal()
    }

    // ---------- Reorder ----------

    override suspend fun reorderTasksForDate(
        dateEpochDay: Long,
        fromIndex: Int,
        toIndex: Int,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (fromIndex == toIndex) return@runCatching

            val current = local.observeByDate(date = dateEpochDay).first()
            if (fromIndex !in current.indices || toIndex !in current.indices) return@runCatching

            val reordered = current.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }

            val start = minOf(fromIndex, toIndex)
            val end = maxOf(fromIndex, toIndex)

            val updates = (start..end).map { index ->
                reordered[index].id to index
            }

            local.updateOrderIndices(updates)

            // Mark affected tasks as pending update if they were synced.
            val affectedIds = updates.map { it.first }.toSet()
            current.filter { it.id in affectedIds && it.syncStatus == SyncStatus.SYNCED }
                .forEach { local.updateSyncStatus(id = it.id, status = SyncStatus.PENDING_UPDATE) }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun deleteAllTasks(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            local.deleteAll()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) },
        )
    }

    override suspend fun syncLocalTasksToServer(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            local.getPendingDeletes().forEach { entity ->
                val remoteId = entity.remoteId
                if (remoteId == null) {
                    // never existed remotely
                    local.deleteById(entity.id)
                } else {
                    remote.deleteTask(remoteId).getOrThrow()
                    local.deleteById(entity.id)
                }
            }

            local.getPendingCreates().forEach { entity ->
                val created = remote.createTask(entity.toTaskPersonal().toRequest()).getOrThrow()
                local.markCreatedSynced(id = entity.id, remoteId = created.id)
            }

            local.getPendingUpdates().forEach { entity ->
                val remoteId = entity.remoteId
                    ?: error(
                        "syncLocalTasksToServer PENDING_UPDATE has null remoteId " +
                            "id=${entity.id}"
                    )
                remote.updateTask(entity.toTaskPersonal().toUpdateRequest(remoteId)).getOrThrow()
                local.markUpdatedSynced(id = entity.id)
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t ->
                Log.e(TAG, "syncLocalTasksToServer failed", t)
                Result.failure(DomainException.fromThrowable(t))
            },
        )
    }

    override suspend fun syncRemoteTasksToLocal(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val remoteTasks = remote.getTasks().getOrThrow()

            for (remoteTask in remoteTasks) {
                val existing = local.getByRemoteId(remoteTask.id)
                if (existing == null) {
                    val orderIndex = nextOrderForDate(LocalDate.ofEpochDay(remoteTask.date))
                    local.insert(remoteTask.toEntity(orderIndex = orderIndex))
                }
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t ->
                Log.e(TAG, "syncRemoteTasksToLocal failed", t)
                Result.failure(DomainException.fromThrowable(t))
            },
        )
    }

    private suspend fun nextOrderForDate(date: LocalDate): Int {
        val currentMax = local
            .observeByDate(date = date.toEpochDay())
            .first()
            .maxOfOrNull { it.orderIndex }
            ?: -1
        return currentMax + 1
    }

    private fun scheduleTaskReminders(
        taskEntity: PersonalTaskEntity,
        remindBeforeMinutes: List<Long> = DEFAULT_REMINDER_MINUTES,
    ) {
        remindBeforeMinutes.forEach { minutes ->
            alarmScheduler.schedule(
                taskEntity.toAlarmItem(remindBeforeMinutes = minutes),
                type = AlarmType.TASK
            )
        }
    }

    private fun observeTasksYearToDate(
        date: LocalDate,
        isCompleted: Boolean
    ): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return local.countInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = isCompleted,
        )
    }

    companion object {
        private const val TAG = "PersonalTaskRepository"
        private val DEFAULT_REMINDER_MINUTES = listOf(0L, 1L, 2L, 5L, 10L)

        private const val DAYS_TO_ADD = 6
        private const val DAYS_IN_WEEK = 7
    }
}
