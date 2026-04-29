package com.todoapp.mobile.data.ai

import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.Schema

object ChatToolNames {
    const val GET_TODAYS_TASKS = "getTodaysTasks"
    const val GET_OVERDUE_TASKS = "getOverdueTasks"
    const val GET_TASKS_FOR_DATE_RANGE = "getTasksForDateRange"
    const val GET_GROUPS = "getGroups"
    const val GET_COMPLETED_TASKS_THIS_WEEK = "getCompletedTasksThisWeek"

    // v2 — write tools
    const val GET_CURRENT_DATE = "getCurrentDate"
    const val CREATE_TASK = "createTask"
    const val UPDATE_TASK = "updateTask"
    const val DELETE_TASK = "deleteTask"
    const val SET_TASK_COMPLETION = "setTaskCompletion"
    const val SET_TASK_SECRET = "setTaskSecret"
}

object ChatToolDeclarations {
    private const val CATEGORY_HINT =
        "Allowed values: SHOPPING, MEDICINE, HEALTH, WORK, STUDY, BIRTHDAY, PERSONAL, OTHER. Defaults to PERSONAL."
    private const val RECURRENCE_HINT =
        "Allowed values: NONE, DAILY, WEEKLY, MONTHLY, YEARLY. Defaults to NONE."

    val all: List<FunctionDeclaration> = listOf(
        FunctionDeclaration(
            name = ChatToolNames.GET_TODAYS_TASKS,
            description = "Return the user's to-do tasks scheduled for today, including pending and completed " +
                "ones. Use this whenever the user asks about today, their day, what they have on their plate now, " +
                "or what they should do next.",
            parameters = emptyMap(),
        ),
        FunctionDeclaration(
            name = ChatToolNames.GET_OVERDUE_TASKS,
            description = "Return the user's tasks that are still incomplete and whose due date is before today. " +
                "Use this when the user asks about missed, overdue, or skipped work.",
            parameters = emptyMap(),
        ),
        FunctionDeclaration(
            name = ChatToolNames.GET_TASKS_FOR_DATE_RANGE,
            description = "Return tasks whose due date falls within an inclusive date range. Use this for any " +
                "question about a future window such as 'this week', 'this weekend', 'next Monday', or a specific " +
                "month.",
            parameters = mapOf(
                "startDate" to Schema.string(
                    "Inclusive start of the range in ISO-8601 format (YYYY-MM-DD).",
                ),
                "endDate" to Schema.string(
                    "Inclusive end of the range in ISO-8601 format (YYYY-MM-DD).",
                ),
            ),
        ),
        FunctionDeclaration(
            name = ChatToolNames.GET_GROUPS,
            description = "Return the family/team groups the user belongs to, including their role, member " +
                "count, and number of pending shared tasks. Use this when the user asks about their groups, " +
                "shared lists, or family/team work.",
            parameters = emptyMap(),
        ),
        FunctionDeclaration(
            name = ChatToolNames.GET_COMPLETED_TASKS_THIS_WEEK,
            description = "Return the count of tasks the user has completed in the current calendar week. Use " +
                "this when the user asks about progress, how productive they have been, or how the week is going.",
            parameters = emptyMap(),
        ),
        FunctionDeclaration(
            name = ChatToolNames.GET_CURRENT_DATE,
            description = "Return today's date and day of week. ALWAYS call this before any tool that takes a " +
                "date argument when the user said 'today', 'tomorrow', 'this Friday', or any other relative " +
                "date — your training data is stale, so you cannot reliably know today's date without calling " +
                "this.",
            parameters = emptyMap(),
        ),
        FunctionDeclaration(
            name = ChatToolNames.CREATE_TASK,
            description = "Create a new task in the user's list. Use this whenever the user asks to add, " +
                "schedule, plan, or remind themselves about something. Always confirm the created task's title " +
                "and date in your reply.",
            parameters = mapOf(
                "title" to Schema.string(
                    "Required. Short, human-readable task title (e.g. 'Buy groceries').",
                ),
                "date" to Schema.string(
                    "Required. Due date in ISO-8601 format (YYYY-MM-DD).",
                ),
                "timeStart" to Schema.string(
                    "Optional. Start time in HH:mm 24-hour format. Defaults to 09:00.",
                ),
                "timeEnd" to Schema.string(
                    "Optional. End time in HH:mm 24-hour format. Defaults to 30 minutes after timeStart.",
                ),
                "description" to Schema.string(
                    "Optional. Longer notes or description for the task.",
                ),
                "category" to Schema.string("Optional. $CATEGORY_HINT"),
                "recurrence" to Schema.string("Optional. $RECURRENCE_HINT"),
                "isSecret" to Schema.boolean(
                    "Optional. If true, marks the task as secret (hidden behind biometric unlock). " +
                        "Defaults to false.",
                ),
            ),
        ),
        FunctionDeclaration(
            name = ChatToolNames.UPDATE_TASK,
            description = "Edit an existing task. Provide only the fields the user wants to change — fields " +
                "you omit stay unchanged. Always state the task id and what fields you changed in your reply. " +
                "If you don't know the task's id, look it up first via getTodaysTasks, getOverdueTasks, or " +
                "getTasksForDateRange.",
            parameters = mapOf(
                "taskId" to Schema.long("Required. The numeric id of the task to update."),
                "title" to Schema.string("Optional. New title."),
                "date" to Schema.string("Optional. New due date in ISO-8601 format (YYYY-MM-DD)."),
                "timeStart" to Schema.string("Optional. New start time in HH:mm 24-hour format."),
                "timeEnd" to Schema.string("Optional. New end time in HH:mm 24-hour format."),
                "description" to Schema.string("Optional. New description text."),
                "category" to Schema.string("Optional. $CATEGORY_HINT"),
                "recurrence" to Schema.string("Optional. $RECURRENCE_HINT"),
                "isSecret" to Schema.boolean("Optional. New value for the secret flag."),
            ),
        ),
        FunctionDeclaration(
            name = ChatToolNames.DELETE_TASK,
            description = "Permanently delete a task by id. Always state the id and the title of the deleted " +
                "task in your reply. If you don't know the id, look it up first.",
            parameters = mapOf(
                "taskId" to Schema.long("Required. The numeric id of the task to delete."),
            ),
        ),
        FunctionDeclaration(
            name = ChatToolNames.SET_TASK_COMPLETION,
            description = "Mark a task as done or undone. Use isCompleted=true to mark done, false to re-open. " +
                "If you don't know the id, look it up first.",
            parameters = mapOf(
                "taskId" to Schema.long("Required. The numeric id of the task to update."),
                "isCompleted" to Schema.boolean("Required. true to mark done, false to mark not done."),
            ),
        ),
        FunctionDeclaration(
            name = ChatToolNames.SET_TASK_SECRET,
            description = "Toggle the secret flag on a task. Secret tasks are hidden behind biometric " +
                "authentication. If you don't know the task's id, look it up first.",
            parameters = mapOf(
                "taskId" to Schema.long("Required. The numeric id of the task to update."),
                "isSecret" to Schema.boolean(
                    "Required. true to mark the task secret, false to remove the secret flag.",
                ),
            ),
        ),
    )
}
