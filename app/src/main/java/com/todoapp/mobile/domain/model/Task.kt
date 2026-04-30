package com.todoapp.mobile.domain.model

import androidx.compose.runtime.Immutable
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Immutable
data class Task(
    val id: Long = 0L,
    val remoteId: Long? = null,
    val title: String,
    val description: String?,
    val date: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val isCompleted: Boolean,
    val isSecret: Boolean,
    val photoUrls: List<String> = emptyList(),
    /**
     * Minutes before timeStart at which to fire the reminder. 0 = on time,
     * positive = N minutes before, null = no reminder. Stored locally; not synced
     * to the backend (alarms are device-local).
     */
    val reminderOffsetMinutes: Long? = 0L,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val customCategoryName: String? = null,
    val recurrence: Recurrence = Recurrence.NONE,
    /**
     * When true, the task spans the whole day; timeStart/timeEnd are placeholders
     * (00:00 / 23:59) and notifications fire at the user's default morning hour.
     */
    val isAllDay: Boolean = false,
)

fun Task.toAlarmItem(remindBeforeMinutes: Long = 0): AlarmItem = AlarmItem(
    time = LocalDateTime.of(date, timeStart.minusMinutes(remindBeforeMinutes)),
    message = title,
    minutesBefore = remindBeforeMinutes,
    taskId = id,
)

fun Task.toCreateTaskRequestDto(
    familyGroupId: Long? = null,
    assignedToUserId: Long? = null,
    priority: String? = null,
): TaskRequest = TaskRequest(
    id = if (id != 0L) id else null,
    title = title,
    description = description,
    date = date.toEpochDay(),
    timeStart = timeStart.toSecondOfDay().toLong(),
    timeEnd = timeEnd.toSecondOfDay().toLong(),
    isCompleted = isCompleted,
    isSecret = isSecret,
    familyGroupId = familyGroupId,
    assignedToUserId = assignedToUserId,
    priority = priority,
    category = category.name,
    customCategoryName = customCategoryName,
    recurrence = recurrence.name,
)

fun TaskData.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    date = LocalDate.ofEpochDay(date),
    timeStart = LocalTime.ofSecondOfDay(timeStart),
    timeEnd = LocalTime.ofSecondOfDay(timeEnd),
    isCompleted = isCompleted,
    isSecret = isSecret,
    photoUrls = photoUrls,
    category = TaskCategory.fromStorage(category),
    customCategoryName = customCategoryName,
    recurrence = Recurrence.fromStorage(recurrence),
)
