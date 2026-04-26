package com.todoapp.mobile.domain.model

enum class TaskCategory {
    SHOPPING,
    MEDICINE,
    HEALTH,
    WORK,
    STUDY,
    BIRTHDAY,
    PERSONAL,
    OTHER,
    ;

    companion object {
        fun fromStorage(value: String?): TaskCategory = value?.let { runCatching { valueOf(it) }.getOrNull() } ?: PERSONAL
    }
}
