package com.todoapp.uikit.components

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
import java.time.LocalDate

@Composable
fun TDTaskCardListByDay(
    modifier: Modifier = Modifier,
    date: LocalDate,
    tasks: List<TaskCardItem>,
) {
    Row(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(30.dp),
        ) {
            Spacer(Modifier.height(4.dp))
            TDText(
                text =
                    date.dayOfWeek
                        .name
                        .take(3) // Thu
                        .lowercase()
                        .replaceFirstChar { it.uppercase() },
                style = TDTheme.typography.subheading4,
                color = TDTheme.colors.gray,
            )
            TDText(
                text = date.dayOfMonth.toString(),
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
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

data class TaskCardItem(
    val taskTitle: String,
    val taskTimeStart: String,
    val taskTimeEnd: String,
)

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
                        taskTitle = "Read Book",
                        taskTimeStart = "09:30",
                        taskTimeEnd = "10:15",
                    ),
                    TaskCardItem(
                        taskTitle = "Gym",
                        taskTimeStart = "18:00",
                        taskTimeEnd = "19:00",
                    ),
                    TaskCardItem(
                        taskTitle = "Study Kotlin",
                        taskTimeStart = "21:00",
                        taskTimeEnd = "22:30",
                    ),
                ),
            ),
        )
    }
}
