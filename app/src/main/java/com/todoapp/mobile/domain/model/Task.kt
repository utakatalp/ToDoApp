package com.todoapp.mobile.domain.model

import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.request.TaskRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String?,
    val date: LocalDate,
    val timeStart: LocalTime,
    val timeEnd: LocalTime,
    val isCompleted: Boolean,
    val isSecret: Boolean
)

fun Task.toAlarmItem(remindBeforeMinutes: Long = 0): AlarmItem {
    return AlarmItem(
        time = LocalDateTime.of(date, timeStart.minusMinutes(remindBeforeMinutes)),
        message = title,
        minutesBefore = remindBeforeMinutes,
    )
}

fun Task.toCreateTaskRequestDto(): TaskRequest {
    return TaskRequest(
        title = title,
        description = description,
        date = date.toEpochDay(),
        timeStart = timeStart.toSecondOfDay().toLong(),
        timeEnd = timeEnd.toSecondOfDay().toLong(),
        isCompleted = isCompleted,
        isSecret = isSecret,
    )
}

fun TaskData.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        date = LocalDate.ofEpochDay(date),
        timeStart = LocalTime.ofSecondOfDay(timeStart),
        timeEnd = LocalTime.ofSecondOfDay(timeEnd),
        isCompleted = isCompleted,
        isSecret = isSecret,
    )
}
