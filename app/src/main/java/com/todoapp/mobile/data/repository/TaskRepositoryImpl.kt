package com.todoapp.mobile.data.repository

import android.util.Log
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSource
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toDomain
import com.todoapp.mobile.domain.repository.CompletedCountByDay
import com.todoapp.mobile.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val remoteDataSource: TaskRemoteDataSource,
    private val localDataSource: TaskLocalDataSource,
) : TaskRepository {
    override fun observeAllTaskEntities(): Flow<List<TaskEntity>> {
        return localDataSource.observeAll()
    }

    override fun observeAllTasks(): Flow<List<Task>> {
        return observeAllTaskEntities().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeRange(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<Task>> {
        return localDataSource.observeRange(
            startDate = startDate.toEpochDay(),
            endDate = endDate.toEpochDay(),
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeTasksByDate(date: LocalDate): Flow<List<Task>> {
        return localDataSource.observeByDate(date = date.toEpochDay()).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun countCompletedTasksInAWeek(
        date: LocalDate,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.countInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun countCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<CompletedCountByDay>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.observeCompletedCountsByDay(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
        )
            .map { rows: List<DayCount> ->
                rows.map { row ->
                    CompletedCountByDay(
                        date = LocalDate.ofEpochDay(row.date),
                        count = row.count
                    )
                }
            }
    }

    override fun countCompletedTasksYearToDate(date: LocalDate): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return localDataSource.countInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = true,
        )
    }

    override fun observePendingTasksYearToDate(date: LocalDate): Flow<Int> {
        val yearStart = date.withDayOfYear(1)
        return localDataSource.countInRange(
            startDate = yearStart.toEpochDay(),
            endDate = date.toEpochDay(),
            isCompleted = false,
        )
    }

    override fun observePendingTasksInAWeek(
        date: LocalDate,
    ): Flow<Int> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.countInRange(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = false,
        )
    }

    override fun observeCompletedCountsByDayInAWeek(date: LocalDate): Flow<List<Int>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        return countCompletedCountsByDayInAWeek(date).map { dayCounts ->
            val map = dayCounts.associate { it.date to it.count }
            (0 until DAYS_IN_WEEK).map { dayOffset ->
                map[weekStart.plusDays(dayOffset.toLong())] ?: 0
            }
        }
    }

    override suspend fun insert(task: Task) {
        remoteDataSource.addTask(task)
            .onSuccess { remoteTask ->
                val entity = remoteTask.toDomain().toEntity()
                localDataSource.insert(withInitializedOrder(entity))
            }
            .onFailure {
                val entity = task.toEntity(SyncStatus.PENDING_CREATE)
                localDataSource.insert(withInitializedOrder(entity))
            }
    }

    override suspend fun delete(task: Task) {
        val taskEntity = localDataSource.getTaskById(task.id)
        taskEntity?.let { taskEntity ->
            if (taskEntity.syncStatus != SyncStatus.SYNCED) {
                localDataSource.delete(taskEntity)
                return
            }
            remoteDataSource.deleteTask(taskEntity.remoteId!!)
                .onSuccess {
                    localDataSource.delete(taskEntity)
                }
                .onFailure {
                    localDataSource.update(taskEntity.copy(syncStatus = SyncStatus.PENDING_DELETE))
                }
        }
    }

    override suspend fun updateTaskCompletion(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        localDataSource.updateTaskCompletion(id, isCompleted)
    }

    override suspend fun getTaskById(id: Long): Task? = withContext(Dispatchers.IO) {
        localDataSource.getTaskById(id)?.toDomain()
    }

    override suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        val taskEntity = localDataSource.getTaskById(task.id)

        if (taskEntity?.syncStatus != SyncStatus.SYNCED) {
            // no need to update remote because its not synced
            localDataSource.update(task.toEntity(SyncStatus.PENDING_CREATE))
            return@withContext
        }

        remoteDataSource.updateTask(taskEntity.remoteId!!, taskEntity.toDomain())
            .onSuccess {
                localDataSource.update(it.toDomain().toEntity(SyncStatus.SYNCED))
            }
            .onFailure {
                localDataSource.update(task.toEntity().copy(syncStatus = SyncStatus.PENDING_UPDATE))
            }
    }

    override suspend fun syncRemoteTasksWithLocal(): Result<Unit> {
        val remoteTasks = remoteDataSource.getTasks().fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        val localTasks = localDataSource.observeAll().first()
        val remoteNotInLocalTasks = remoteTasks.tasks.filter { remoteTask ->
            localTasks.none { localTask ->
                localTask.remoteId == remoteTask.id
            }
        }
        Log.d("syncRemoteTasksWithLocal", remoteNotInLocalTasks.toString())

        suspend fun next(dateEpochDay: Long): Int {
            val current = localDataSource
                .observeByDate(date = dateEpochDay)
                .first()
                .maxOfOrNull { it.orderIndex }
                ?: -1
            val n = current + 1
            return n
        }

        val addedTaskEntities = remoteNotInLocalTasks
            .map { it.toDomain().toEntity() }
            .map { entity ->
                if (entity.orderIndex != 0) {
                    entity
                } else {
                    entity.copy(orderIndex = next(entity.date))
                }
            }

        localDataSource.insertAll(addedTaskEntities)

        return Result.success(Unit)
    }

    override suspend fun syncLocalTasksToServer(): Result<Unit> = withContext(Dispatchers.IO) {
        val nonSyncedTasks = findNonSyncedTasks()
        nonSyncedTasks.forEach { taskEntity ->
            syncTask(taskEntity).onFailure {
                Log.e("syncLocalTasksToServer", it.message ?: "Unknown error")
                return@withContext Result.failure(it)
            }
        }
        Result.success(Unit)
    }

    override suspend fun syncTask(taskEntity: TaskEntity): Result<Unit> {
        return when (taskEntity.syncStatus) {
            SyncStatus.SYNCED -> {
                Result.success(Unit)
            }
            SyncStatus.PENDING_CREATE -> syncCreatedTask(taskEntity)
            SyncStatus.PENDING_UPDATE -> syncUpdatedTask(taskEntity)
            SyncStatus.PENDING_DELETE -> syncDeletedTask(taskEntity)
        }
    }

    override suspend fun deleteAllTasks(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            localDataSource.deleteAll()
            Result.success(Unit)
        } catch (t: Throwable) {
            Result.failure(DomainException.fromThrowable(t))
        }
    }

    override suspend fun getAllTasks(): Result<Unit> = withContext(Dispatchers.IO) {
        remoteDataSource.getTasks().fold(
            onSuccess = { tasks ->
                runCatching {
                    val nextByDate = mutableMapOf<Long, Int>()
                    suspend fun next(dateEpochDay: Long): Int {
                        val current = nextByDate[dateEpochDay]
                            ?: (
                                localDataSource.observeByDate(date = dateEpochDay)
                                .first()
                                .maxOfOrNull { it.orderIndex }
                                ?: -1
                            )
                        val n = current + 1
                        nextByDate[dateEpochDay] = n
                        return n
                    }

                    val entities = tasks.tasks
                        .map { it.toDomain().toEntity() }
                        .map { entity ->
                            if (entity.orderIndex != 0) {
                                entity
                            } else {
                                entity.copy(orderIndex = next(entity.date))
                            }
                        }

                    localDataSource.insertAll(entities)
                    Unit
                }.fold(
                    onSuccess = { Result.success(Unit) },
                    onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) }
                )
            },
            onFailure = { t ->
                Result.failure(DomainException.fromThrowable(t))
            }
        )
    }

    private suspend fun syncDeletedTask(taskEntity: TaskEntity): Result<Unit> {
        val remoteResult = remoteDataSource.deleteTask(taskEntity.remoteId!!)

        return remoteResult.fold(
            onSuccess = {
                runCatching { localDataSource.delete(taskEntity) }
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    private suspend fun syncUpdatedTask(taskEntity: TaskEntity): Result<Unit> {
        val remoteResult = remoteDataSource.updateTask(taskEntity.remoteId!!, taskEntity.toDomain())

        return remoteResult.fold(
            onSuccess = { remoteTask ->
                val updated = taskEntity.copy(
                    remoteId = remoteTask.id,
                    syncStatus = SyncStatus.SYNCED
                )

                runCatching { localDataSource.update(updated) }
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    private suspend fun syncCreatedTask(taskEntity: TaskEntity): Result<Unit> {
        val remoteResult = remoteDataSource.addTask(taskEntity.toDomain())

        return remoteResult.fold(
            onSuccess = { remoteTask ->
                val updated = taskEntity.copy(
                    id = remoteTask.id,
                    remoteId = remoteTask.id,
                    syncStatus = SyncStatus.SYNCED
                )

                runCatching {
                    localDataSource.update(updated)
                }
            },
            onFailure = {
                Result.failure(it)
            }
        )
    }

    override suspend fun findNonSyncedTasks(): List<TaskEntity> {
        val tasks = observeAllTaskEntities().first()
        val nonSyncedTasks = tasks.filter { it.syncStatus != SyncStatus.SYNCED }
        return nonSyncedTasks
    }

    private suspend fun nextOrderForDate(dateEpochDay: Long): Int {
        val current = localDataSource.observeByDate(date = dateEpochDay)
            .first()
            .maxOfOrNull { it.orderIndex }
            ?: -1
        return current + 1
    }

    /**
     * Assigns orderIndex if the entity doesn't already carry a meaningful order.
     */
    private suspend fun withInitializedOrder(entity: TaskEntity): TaskEntity {
        return if (entity.orderIndex != 0) {
            entity
        } else {
            entity.copy(
            orderIndex = nextOrderForDate(
                entity.date
            )
        )
        }
    }

    override suspend fun reorderTasksForDate(
        date: LocalDate,
        fromIndex: Int,
        toIndex: Int,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (fromIndex == toIndex) return@runCatching

            val current = localDataSource
                .observeByDate(date.toEpochDay())
                .first()

            if (fromIndex !in current.indices || toIndex !in current.indices) {
                return@runCatching
            }

            val reordered = current.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            }

            val start = minOf(fromIndex, toIndex)
            val end = maxOf(fromIndex, toIndex)

            val updates = (start..end).map { index ->
                reordered[index].id to index
            }

            localDataSource.updateOrderIndices(updates)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) }
        )
    }

    companion object {
        private const val DAYS_TO_ADD = 6
        private const val DAYS_IN_WEEK = 7
    }
}
