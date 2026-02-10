package com.todoapp.mobile.domain.alarm

import com.todoapp.mobile.domain.model.AlarmItem

enum class AlarmType {
    TASK,
    DAILY_PLAN
}

interface AlarmScheduler {
    fun schedule(item: AlarmItem, type: AlarmType)
    fun cancelTask(item: AlarmItem)
    fun cancelScheduledAlarm(type: AlarmType)
}
