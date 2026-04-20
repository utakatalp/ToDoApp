package com.todoapp.mobile.data.repository

import android.util.Log
import com.todoapp.mobile.common.DomainException
import com.todoapp.mobile.data.mapper.toDomain
import com.todoapp.mobile.data.mapper.toEntity
import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.data.source.local.DayCount
import com.todoapp.mobile.data.source.local.datasource.GroupTaskLocalDataSource
import com.todoapp.mobile.data.source.local.datasource.TaskLocalDataSource
import com.todoapp.mobile.data.source.remote.datasource.TaskRemoteDataSource
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.toDomain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
    private val groupTaskLocalDataSource: GroupTaskLocalDataSource,
    private val todoApi: com.todoapp.mobile.data.source.remote.api.ToDoApi,
) : TaskRepository {

    private val _taskPhotoUrls = kotlinx.coroutines.flow.MutableStateFlow<Map<Long, List<String>>>(emptyMap())

    override fun observeTaskPhotoUrls(): Flow<Map<Long, List<String>>> = _taskPhotoUrls
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

    override fun observePendingCountsByDayInAWeek(date: LocalDate): Flow<List<Int>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.observePendingCountsByDay(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
        ).map { rows ->
            val map = rows.associate { LocalDate.ofEpochDay(it.date) to it.count }
            (0 until DAYS_IN_WEEK).map { offset -> map[weekStart.plusDays(offset.toLong())] ?: 0 }
        }
    }

    override suspend fun insert(task: Task) {
        remoteDataSource.addTask(task)
            .onSuccess { remoteTask ->
                val entity = remoteTask.toDomain().toEntity().copy(id = 0L)
                localDataSource.insert(withInitializedOrder(entity))
            }
            .onFailure {
                val entity = task.toEntity(SyncStatus.PENDING_CREATE)
                localDataSource.insert(withInitializedOrder(entity))
            }
    }

    override suspend fun insertWithPhotos(task: Task, photos: List<Pair<ByteArray, String>>): Result<Unit> {
        // Unlike insert(), this requires a successful server create so we have a remoteId to
        // attach the photos to. If the network call fails, we surface the failure upwards.
        return remoteDataSource.addTask(task).mapCatching { remoteTask ->
            val remoteId = remoteTask.id
            val entity = remoteTask.toDomain().toEntity().copy(id = 0L)
            localDataSource.insert(withInitializedOrder(entity))
            for ((bytes, mime) in photos) {
                uploadTaskPhoto(remoteId, bytes, mime).getOrNull()  // best effort per photo
            }
            // final refresh once all uploads attempted
            refreshPhotoUrlsForTask(remoteId)
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

    override suspend fun fetchRemoteTask(id: Long): Result<Task> {
        return com.todoapp.mobile.common.handleRequest { todoApi.getTaskById(id) }
            .map { it.toDomain() }
    }

    override suspend fun uploadTaskPhoto(taskId: Long, bytes: ByteArray, mimeType: String): Result<String> {
        val body = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = okhttp3.MultipartBody.Part.createFormData("file", "photo.jpg", body)
        return com.todoapp.mobile.common.handleRequest { todoApi.uploadTaskPhoto(taskId, part) }
            .map { it.url }
            .onSuccess { refreshPhotoUrlsForTask(taskId) }
    }

    override suspend fun deleteTaskPhoto(taskId: Long, photoId: Long): Result<Unit> {
        return com.todoapp.mobile.common.handleRequest { todoApi.deleteTaskPhoto(taskId, photoId) }
            .onSuccess { refreshPhotoUrlsForTask(taskId) }
    }

    /** Pull the current photo URL list for a single task and patch the in-memory map. */
    private suspend fun refreshPhotoUrlsForTask(taskId: Long) {
        com.todoapp.mobile.common.handleRequest { todoApi.getTaskById(taskId) }
            .onSuccess { data ->
                val current = _taskPhotoUrls.value.toMutableMap()
                if (data.photoUrls.isEmpty()) current.remove(taskId) else current[taskId] = data.photoUrls
                _taskPhotoUrls.value = current
            }
    }

    override suspend fun update(task: Task) = withContext(Dispatchers.IO) {
        val taskEntity = localDataSource.getTaskById(task.id)

        if (taskEntity?.syncStatus != SyncStatus.SYNCED) {
            // no need to update remote because its not synced
            localDataSource.update(
                task.toEntity(SyncStatus.PENDING_CREATE).copy(remoteId = taskEntity?.remoteId)
            )
            return@withContext
        }

        remoteDataSource.updateTask(taskEntity.remoteId!!, taskEntity.toDomain())
            .onSuccess { remoteTask ->
                localDataSource.update(
                    remoteTask.toDomain().toEntity(SyncStatus.SYNCED).copy(id = taskEntity.id)
                )
            }
            .onFailure {
                localDataSource.update(
                    task.toEntity(SyncStatus.PENDING_UPDATE).copy(remoteId = taskEntity.remoteId)
                )
            }
    }

    override suspend fun syncRemoteTasksWithLocal(): Result<Unit> {
        val remoteTasks = remoteDataSource.getTasks().fold(
            onSuccess = { it },
            onFailure = { return Result.failure(it) }
        )

        // Refresh the in-memory photo-url map so Home/Calendar can show thumbnails.
        _taskPhotoUrls.value = remoteTasks.tasks
            .filter { it.photoUrls.isNotEmpty() }
            .associate { it.id to it.photoUrls }

        val localTasks = localDataSource.observeAll().first()
        val remoteNotInLocalTasks = remoteTasks.tasks.filter { remoteTask ->
            remoteTask.familyGroupId == null &&
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
            .map { it.toDomain().toEntity().copy(id = 0L) }
            .map { entity ->
                if (entity.orderIndex != 0) {
                    entity
                } else {
                    entity.copy(orderIndex = next(entity.date))
                }
            }

        return runCatching {
            localDataSource.insertAll(addedTaskEntities)
            // Safety net: purge any personal-task rows whose remoteId actually belongs to a
            // group task (from stale data on earlier builds). Keeps Home free of dups.
            val groupRemoteIds = groupTaskLocalDataSource.getAllRemoteIds()
            if (groupRemoteIds.isNotEmpty()) localDataSource.deleteByRemoteIds(groupRemoteIds)
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { t -> Result.failure(DomainException.fromThrowable(t)) }
        )
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
            SyncStatus.SYNCED -> Result.success(Unit)
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
                        .map { it.toDomain().toEntity().copy(id = 0L) }
                        .map { entity ->
                            if (entity.orderIndex != 0) {
                                entity
                            } else {
                                entity.copy(orderIndex = next(entity.date))
                            }
                        }

                    localDataSource.insertAll(entities)
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

    override fun searchTasks(query: String): Flow<List<Task>> {
        val likeQuery = "%$query%"
        return localDataSource.search(likeQuery).map { list -> list.map { it.toDomain() } }
    }

    override fun observeTasksByWeekAndStatus(date: LocalDate, isCompleted: Boolean): Flow<List<Task>> {
        val weekStart = date.with(DayOfWeek.MONDAY)
        val weekEnd = weekStart.plusDays(DAYS_TO_ADD.toLong())
        return localDataSource.observeByWeekAndStatus(
            startDate = weekStart.toEpochDay(),
            endDate = weekEnd.toEpochDay(),
            isCompleted = isCompleted,
        ).map { list -> list.map { it.toDomain() } }
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
