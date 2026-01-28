package com.todoapp.mobile.domain.alarm

import com.todoapp.mobile.domain.model.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}
