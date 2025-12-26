package com.todoapp.uikit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDTaskCard(
    modifier: Modifier = Modifier,
    taskTitle: String,
    taskTimeStart: String,
    taskTimeEnd: String,
) {
    val randNumber = (1..2).random()
    val randVerticalColor = if (randNumber == 1) TDTheme.colors.purple else TDTheme.colors.red

    Row(
        modifier =
            modifier
                .height(60.dp)
                .fillMaxWidth(),
    ) {
        VerticalDivider(
            thickness = 4.dp,
            color = randVerticalColor,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            TDText(
                text = taskTitle,
                style = TDTheme.typography.heading7,
            )
            Spacer(Modifier.weight(1f))
            TDText(
                text = "$taskTimeStart - $taskTimeEnd",
                style = TDTheme.typography.subheading1,
                color = TDTheme.colors.gray,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 140)
@Composable
private fun TDTaskCardPreview() {
    TDTheme {
        TDTaskCard(
            taskTitle = "Read Book",
            taskTimeStart = "09:30",
            taskTimeEnd = "10:15",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TDTaskCardPreview_LongTitle() {
    TDTheme {
        TDTaskCard(
            modifier = Modifier.fillMaxWidth(),
            taskTitle = "Prepare Presentation Slides",
            taskTimeStart = "14:00",
            taskTimeEnd = "16:30",
        )
    }
}
