package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme
import java.time.LocalDate

@Composable
fun TDTaskCardList(
    modifier: Modifier = Modifier,
    tasks: List<TaskDayItem>,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        tasks.forEach { item ->
            TDTaskCardListByDay(
                date = item.date,
                tasks = item.tasks,
            )
        }
    }
}

data class TaskDayItem(
    val date: LocalDate,
    val tasks: List<TaskCardItem>,
)

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TDTaskCardListPreview() {
    TDTheme {
        TDTaskCardList(
            modifier = Modifier.padding(16.dp),
            tasks =
                listOf(
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 12),
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
                            ),
                    ),
                    TaskDayItem(
                        date = LocalDate.of(2025, 1, 13),
                        tasks =
                            listOf(
                                TaskCardItem(
                                    taskTitle = "Study Kotlin",
                                    taskTimeStart = "10:00",
                                    taskTimeEnd = "12:00",
                                ),
                            ),
                    ),
                ),
        )
    }
}
