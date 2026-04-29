package com.todoapp.mobile.data.ai

import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.todoapp.mobile.domain.model.Group
import com.todoapp.mobile.domain.model.Recurrence
import com.todoapp.mobile.domain.model.Task
import com.todoapp.mobile.domain.model.TaskCategory
import com.todoapp.mobile.domain.repository.GroupRepository
import com.todoapp.mobile.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import timber.log.Timber
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatToolRegistry @Inject constructor(
    private val taskRepository: TaskRepository,
    private val groupRepository: GroupRepository,
    private val clock: Clock,
) {
    val declarations: List<FunctionDeclaration> = ChatToolDeclarations.all

    @Suppress("CyclomaticComplexMethod")
    suspend fun execute(call: FunctionCallPart): FunctionResponsePart = try {
        val payload = when (call.name) {
            ChatToolNames.GET_TODAYS_TASKS -> getTodaysTasks()
            ChatToolNames.GET_OVERDUE_TASKS -> getOverdueTasks()
            ChatToolNames.GET_TASKS_FOR_DATE_RANGE -> getTasksForDateRange(call.args)
            ChatToolNames.GET_GROUPS -> getGroups()
            ChatToolNames.GET_COMPLETED_TASKS_THIS_WEEK -> getCompletedTasksThisWeek()
            ChatToolNames.GET_CURRENT_DATE -> getCurrentDate()
            ChatToolNames.CREATE_TASK -> createTask(call.args)
            ChatToolNames.UPDATE_TASK -> updateTask(call.args)
            ChatToolNames.DELETE_TASK -> deleteTask(call.args)
            ChatToolNames.SET_TASK_COMPLETION -> setTaskCompletion(call.args)
            ChatToolNames.SET_TASK_SECRET -> setTaskSecret(call.args)
            else -> errorPayload("Unknown tool: ${call.name}")
        }
        FunctionResponsePart(call.name, payload)
    } catch (t: Throwable) {
        Timber.tag("ChatToolRegistry").w(t, "Tool ${call.name} failed")
        FunctionResponsePart(call.name, errorPayload(t.message ?: "tool failed"))
    }

    private suspend fun getTodaysTasks(): JsonObject {
        val today = LocalDate.now(clock)
        val tasks = taskRepository.observeTasksByDate(today, includeRecurringInstances = true).first()
        return buildJsonObject {
            put("date", today.format(DATE_FORMAT))
            put("count", tasks.size)
            put("tasks", tasks.toJsonArray())
        }
    }

    private suspend fun getOverdueTasks(): JsonObject {
        val today = LocalDate.now(clock)
        val all = taskRepository.observeAllTasks().first()
        val overdue = all.filter { !it.isCompleted && it.date.isBefore(today) }
        return buildJsonObject {
            put("today", today.format(DATE_FORMAT))
            put("count", overdue.size)
            put("tasks", overdue.toJsonArray())
        }
    }

    private suspend fun getTasksForDateRange(args: Map<String, JsonElement>): JsonObject {
        val start = args["startDate"]?.jsonPrimitive?.contentOrNull?.let(::parseDate)
        val end = args["endDate"]?.jsonPrimitive?.contentOrNull?.let(::parseDate)
        return when {
            start == null -> errorPayload("Missing or invalid startDate; must be ISO-8601 (YYYY-MM-DD).")
            end == null -> errorPayload("Missing or invalid endDate; must be ISO-8601 (YYYY-MM-DD).")
            end.isBefore(start) -> errorPayload("endDate ($end) is before startDate ($start).")
            else -> {
                val tasks = taskRepository.observeRange(start, end).first()
                buildJsonObject {
                    put("startDate", start.format(DATE_FORMAT))
                    put("endDate", end.format(DATE_FORMAT))
                    put("count", tasks.size)
                    put("tasks", tasks.toJsonArray())
                }
            }
        }
    }

    private suspend fun getGroups(): JsonObject {
        val groups = groupRepository.observeAllGroups().first()
        return buildJsonObject {
            put("count", groups.size)
            put(
                "groups",
                buildJsonArray {
                    groups.forEach { add(it.toJson()) }
                },
            )
        }
    }

    private suspend fun getCompletedTasksThisWeek(): JsonObject {
        val today = LocalDate.now(clock)
        val count = taskRepository.countCompletedTasksInAWeek(today, includeRecurring = true).first()
        return buildJsonObject {
            put("weekOf", today.format(DATE_FORMAT))
            put("completedCount", count)
        }
    }

    private fun getCurrentDate(): JsonObject {
        val today = LocalDate.now(clock)
        return buildJsonObject {
            put("date", today.format(DATE_FORMAT))
            put("dayOfWeek", today.dayOfWeek.name)
        }
    }

    private suspend fun createTask(args: Map<String, JsonElement>): JsonObject {
        val title = args["title"]?.jsonPrimitive?.contentOrNull
        val date = args["date"]?.jsonPrimitive?.contentOrNull?.let(::parseDate)
        return when {
            title.isNullOrBlank() -> errorPayload("Missing required 'title'.")
            date == null -> errorPayload("Missing or invalid 'date'; must be ISO-8601 (YYYY-MM-DD).")
            else -> {
                val timeStart = args["timeStart"]?.jsonPrimitive?.contentOrNull?.let(::parseTime)
                    ?: LocalTime.of(DEFAULT_START_HOUR, 0)
                val timeEnd = args["timeEnd"]?.jsonPrimitive?.contentOrNull?.let(::parseTime)
                    ?: timeStart.plusMinutes(DEFAULT_DURATION_MINUTES)
                val description = args["description"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
                val category = args["category"]?.jsonPrimitive?.contentOrNull?.let(::parseCategory)
                    ?: TaskCategory.PERSONAL
                val recurrence = args["recurrence"]?.jsonPrimitive?.contentOrNull?.let(::parseRecurrence)
                    ?: Recurrence.NONE
                val isSecret = args["isSecret"]?.jsonPrimitive?.booleanOrNull ?: false

                val task = Task(
                    title = title,
                    description = description,
                    date = date,
                    timeStart = timeStart,
                    timeEnd = timeEnd,
                    isCompleted = false,
                    isSecret = isSecret,
                    category = category,
                    recurrence = recurrence,
                )
                taskRepository.insert(task)
                buildJsonObject {
                    put("created", true)
                    put("title", title)
                    put("date", date.format(DATE_FORMAT))
                    put("timeStart", timeStart.format(TIME_FORMAT))
                    put("timeEnd", timeEnd.format(TIME_FORMAT))
                    put("category", category.name)
                    put("recurrence", recurrence.name)
                    put("isSecret", isSecret)
                }
            }
        }
    }

    @Suppress("CyclomaticComplexMethod")
    private suspend fun updateTask(args: Map<String, JsonElement>): JsonObject {
        val taskId = args["taskId"]?.jsonPrimitive?.longOrNull
        val current = taskId?.let { taskRepository.getTaskById(it) }
        return when {
            taskId == null -> errorPayload("Missing or invalid 'taskId'; must be a number.")
            current == null -> errorPayload("Task $taskId not found.")
            else -> {
                val newTitle = args["title"]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
                val newDate = args["date"]?.jsonPrimitive?.contentOrNull?.let(::parseDate)
                val newStart = args["timeStart"]?.jsonPrimitive?.contentOrNull?.let(::parseTime)
                val newEnd = args["timeEnd"]?.jsonPrimitive?.contentOrNull?.let(::parseTime)
                val newDescription = args["description"]?.jsonPrimitive?.contentOrNull
                val newCategory = args["category"]?.jsonPrimitive?.contentOrNull?.let(::parseCategory)
                val newRecurrence = args["recurrence"]?.jsonPrimitive?.contentOrNull?.let(::parseRecurrence)
                val newSecret = args["isSecret"]?.jsonPrimitive?.booleanOrNull

                val changed = mutableListOf<String>()
                val updated = current.copy(
                    title = newTitle?.also { changed += "title" } ?: current.title,
                    description = if (newDescription != null) {
                        changed += "description"
                        newDescription.takeIf { it.isNotBlank() }
                    } else {
                        current.description
                    },
                    date = newDate?.also { changed += "date" } ?: current.date,
                    timeStart = newStart?.also { changed += "timeStart" } ?: current.timeStart,
                    timeEnd = newEnd?.also { changed += "timeEnd" } ?: current.timeEnd,
                    category = newCategory?.also { changed += "category" } ?: current.category,
                    recurrence = newRecurrence?.also { changed += "recurrence" } ?: current.recurrence,
                    isSecret = newSecret?.also { changed += "isSecret" } ?: current.isSecret,
                )
                if (changed.isEmpty()) {
                    errorPayload("No fields provided to update for task $taskId.")
                } else {
                    taskRepository.update(updated)
                    buildJsonObject {
                        put("id", taskId)
                        put("title", updated.title)
                        putJsonArray("fieldsChanged") { changed.forEach { add(it) } }
                    }
                }
            }
        }
    }

    private suspend fun deleteTask(args: Map<String, JsonElement>): JsonObject {
        val taskId = args["taskId"]?.jsonPrimitive?.longOrNull
        val task = taskId?.let { taskRepository.getTaskById(it) }
        return when {
            taskId == null -> errorPayload("Missing or invalid 'taskId'; must be a number.")
            task == null -> errorPayload("Task $taskId not found.")
            else -> {
                taskRepository.delete(task)
                buildJsonObject {
                    put("id", taskId)
                    put("title", task.title)
                    put("deleted", true)
                }
            }
        }
    }

    private suspend fun setTaskCompletion(args: Map<String, JsonElement>): JsonObject {
        val taskId = args["taskId"]?.jsonPrimitive?.longOrNull
        val isCompleted = args["isCompleted"]?.jsonPrimitive?.booleanOrNull
        return when {
            taskId == null -> errorPayload("Missing or invalid 'taskId'; must be a number.")
            isCompleted == null -> errorPayload("Missing or invalid 'isCompleted'; must be true or false.")
            else -> {
                taskRepository.updateTaskCompletion(taskId, isCompleted)
                buildJsonObject {
                    put("id", taskId)
                    put("isCompleted", isCompleted)
                }
            }
        }
    }

    private suspend fun setTaskSecret(args: Map<String, JsonElement>): JsonObject {
        val taskId = args["taskId"]?.jsonPrimitive?.longOrNull
        val isSecret = args["isSecret"]?.jsonPrimitive?.booleanOrNull
        val task = taskId?.let { taskRepository.getTaskById(it) }
        return when {
            taskId == null -> errorPayload("Missing or invalid 'taskId'; must be a number.")
            isSecret == null -> errorPayload("Missing or invalid 'isSecret'; must be true or false.")
            task == null -> errorPayload("Task $taskId not found.")
            else -> {
                taskRepository.update(task.copy(isSecret = isSecret))
                buildJsonObject {
                    put("id", taskId)
                    put("title", task.title)
                    put("isSecret", isSecret)
                }
            }
        }
    }

    private fun List<Task>.toJsonArray(): JsonElement = buildJsonArray {
        forEach { task -> add(task.toJson()) }
    }

    private fun Task.toJson(): JsonObject = buildJsonObject {
        put("id", id)
        put("title", title)
        description?.let { put("description", it) }
        put("date", date.format(DATE_FORMAT))
        put("timeStart", timeStart.format(TIME_FORMAT))
        put("timeEnd", timeEnd.format(TIME_FORMAT))
        put("isCompleted", isCompleted)
        put("isSecret", isSecret)
        put("category", category.name)
        put("recurrence", recurrence.name)
    }

    private fun Group.toJson(): JsonObject = buildJsonObject {
        put("id", id)
        put("name", name)
        if (description.isNotBlank()) put("description", description)
        put("memberCount", memberCount)
        put("pendingTaskCount", pendingTaskCount)
        if (role.isNotBlank()) put("role", role)
    }

    private fun parseDate(value: String): LocalDate? = runCatching {
        LocalDate.parse(value, DATE_FORMAT)
    }.getOrNull()

    private fun parseTime(value: String): LocalTime? = runCatching {
        LocalTime.parse(value, TIME_FORMAT)
    }.getOrNull()

    private fun parseCategory(value: String): TaskCategory? = runCatching {
        TaskCategory.valueOf(value.trim().uppercase())
    }.getOrNull()

    private fun parseRecurrence(value: String): Recurrence? = runCatching {
        Recurrence.valueOf(value.trim().uppercase())
    }.getOrNull()

    private fun errorPayload(message: String): JsonObject = buildJsonObject {
        put("error", JsonPrimitive(message))
    }

    companion object {
        private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        private val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private const val DEFAULT_START_HOUR = 9
        private const val DEFAULT_DURATION_MINUTES = 30L
    }
}
