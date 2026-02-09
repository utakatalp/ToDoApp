package com.todoapp.mobile.data.mapper

import com.todoapp.mobile.data.model.entity.SyncStatus
import com.todoapp.mobile.data.model.entity.TaskEntity
import com.todoapp.mobile.domain.model.Task
import java.time.LocalDate
import java.time.LocalTime

private const val MINUTE_IN_HOUR = 60
private fun LocalDate.toEpochDayLong(): Long = toEpochDay()
private fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

private fun Long.toLocalTimeFromMinuteOfDay(): LocalTime =
    LocalTime.of((this / MINUTE_IN_HOUR).toInt(), (this % MINUTE_IN_HOUR).toInt())

private fun LocalTime.toMinuteOfDayLong(): Long =
    (hour * MINUTE_IN_HOUR + minute).toLong()

fun TaskEntity.toDomain(): Task =
    Task(
        id = id,
        title = title,
        description = description,
        date = date.toLocalDate(),
        timeStart = timeStart.toLocalTimeFromMinuteOfDay(),
        timeEnd = timeEnd.toLocalTimeFromMinuteOfDay(),
        isCompleted = isCompleted,
        isSecret = isSecret,
    )

fun Task.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): TaskEntity {
    val remoteIdOrNull = if (syncStatus == SyncStatus.SYNCED) id else null

    return TaskEntity(
        title = title,
        description = description,
        date = date.toEpochDayLong(),
        timeStart = timeStart.toMinuteOfDayLong(),
        timeEnd = timeEnd.toMinuteOfDayLong(),
        isCompleted = isCompleted,
        isSecret = isSecret,
        remoteId = remoteIdOrNull,
        syncStatus = syncStatus,
    )
}
