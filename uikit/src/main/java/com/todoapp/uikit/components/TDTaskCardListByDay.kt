package com.todoapp.uikit.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.previews.TDPreview
import com.todoapp.uikit.theme.TDTheme
import androidx.compose.ui.platform.LocalConfiguration
import java.time.LocalDate
import java.time.format.TextStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TDTaskCardListByDay(
    modifier: Modifier = Modifier,
    date: LocalDate,
    tasks: List<TaskCardItem>,
    onTaskClick: (taskId: Long) -> Unit = {},
) {
    val locale = LocalConfiguration.current.locales[0]
    Row(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(4.dp))
            TDText(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                style = TDTheme.typography.subheading4,
                color = TDTheme.colors.gray,
            )
            TDText(
                text = "${date.month.getDisplayName(TextStyle.SHORT, locale)} ${date.dayOfMonth}",
                style = TDTheme.typography.heading3,
                color = TDTheme.colors.onBackground
            )
        }
        Spacer(Modifier.width(20.dp))
        Column {
            tasks.forEach { task ->
                TDTaskCard(
                    taskTitle = task.taskTitle,
                    taskTimeStart = task.taskTimeStart,
                    taskTimeEnd = task.taskTimeEnd,
                    isCompleted = task.isCompleted,
                    description = task.description,
                    onClick = { onTaskClick(task.taskId) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

data class TaskCardItem(
    val taskId: Long,
    val taskTitle: String,
    val taskTimeStart: String,
    val taskTimeEnd: String,
    val isCompleted: Boolean = false,
    val description: String? = null,
)

@RequiresApi(Build.VERSION_CODES.O)
@TDPreview
@Composable
fun TDTaskCardListByDayPreview() {
    TDTheme {
        TDTaskCardListByDay(
            modifier = Modifier.padding(16.dp),
            date = LocalDate.of(2025, 1, 18),
            tasks =
                listOf(
                    TaskCardItem(
                        taskId = 1L,
                        taskTitle = "Read Book",
                        taskTimeStart = "09:30",
                        taskTimeEnd = "10:15",
                    ),
                    TaskCardItem(
                        taskId = 2L,
                        taskTitle = "Gym",
                        taskTimeStart = "18:00",
                        taskTimeEnd = "19:00",
                    ),
                    TaskCardItem(
                        taskId = 3L,
                        taskTitle = "Study Kotlin",
                        taskTimeStart = "21:00",
                        taskTimeEnd = "22:30",
                    ),
                ),
            )
    }
}
