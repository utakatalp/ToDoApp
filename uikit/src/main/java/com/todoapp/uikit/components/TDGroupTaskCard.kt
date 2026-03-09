package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDGroupTaskCard(
    taskTitle: String,
    taskDescription: String?,
    assignedTo: String,
    isCompleted: Boolean,
    timeStart: String,
    timeEnd: String,
    date: String,
    onCheckboxClick: () -> Unit,
) {
    val initial = assignedTo.first().uppercaseChar()
    var backGroundColor = TDTheme.colors.background
    var textDecoration = TextDecoration.None

    if (isCompleted) {
        textDecoration = TextDecoration.LineThrough
        backGroundColor = TDTheme.colors.background.copy(0.60f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backGroundColor)
                .padding(16.dp)
        ) {
            TDCheckBox(
                modifier = Modifier.padding(top = 12.dp),
                isChecked = isCompleted,
                onCheckBoxClick = { onCheckboxClick() }
            )
            Spacer(Modifier.width(12.dp))

            Column {
                TDText(
                    text = taskTitle,
                    style = TDTheme.typography.heading3.copy(
                        textDecoration = textDecoration
                    )
                )

                TDText(
                    text = taskDescription ?: "",
                    style = TDTheme.typography.regularTextStyle.copy(
                        fontSize = 12.sp,
                        textDecoration = textDecoration
                    ),
                    color = TDTheme.colors.lightGray
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(TDTheme.colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial.toString(),
                            color = TDTheme.colors.white,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    if (isCompleted) {
                        TDText(
                            text = "Completed",
                            color = TDTheme.colors.green,
                            style = TDTheme.typography.regularTextStyle
                        )
                    } else {
                        TDText(
                            text = "$timeStart - $timeEnd   $date",
                            color = TDTheme.colors.lightGray,
                            style = TDTheme.typography.regularTextStyle.copy(
                                textDecoration = textDecoration
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TDGroupTaskCardPreview_NotCompleted() {
    TDTheme {
        TDGroupTaskCard(
            taskTitle = "Buy Groceries",
            taskDescription = "Milk, Eggs, Bread",
            assignedTo = "Natalia",
            isCompleted = false,
            timeStart = "09:00",
            timeEnd = "10:00",
            date = "19 Feb 2026",
            onCheckboxClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TDGroupTaskCardPreview_Completed() {
    TDTheme {
        TDGroupTaskCard(
            taskTitle = "Morning Run",
            taskDescription = "5km around the park",
            assignedTo = "Alp",
            isCompleted = true,
            timeStart = "07:00",
            timeEnd = "08:00",
            date = "18 Feb 2026",
            onCheckboxClick = {}
        )
    }
}
