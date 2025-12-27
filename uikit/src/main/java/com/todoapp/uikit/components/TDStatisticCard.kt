package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDStatisticCard(
    text: String,
    taskAmount: Int,
    modifier: Modifier = Modifier,
    isCompleted: Boolean = true,
) {
    if (isCompleted) {
        Column(
            modifier =
                modifier
                    .size(width = 162.dp, height = 96.dp)
                    .background(TDTheme.colors.onBackground),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CheckInBoxComponent()
                Spacer(Modifier.width(10.dp))
                TDText(
                    text = text,
                    color = TDTheme.colors.background,
                    style = TDTheme.typography.subheading1,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(Modifier.weight(1f))
                TDText(
                    fullText = "$taskAmount This Week",
                    spanText = "$taskAmount",
                    spanStyle =
                        SpanStyle(
                            color = TDTheme.colors.background,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        ),
                    style = TDTheme.typography.subheading2,
                    color = TDTheme.colors.statusCardGray,
                )
                Spacer(Modifier.weight(0.6f))
            }
        }
    } else {
        Column(
            modifier =
                modifier
                    .size(width = 162.dp, height = 96.dp)
                    .background(TDTheme.colors.red),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CrossInBoxComponent()
                Spacer(Modifier.width(10.dp))
                TDText(
                    text = text,
                    style = TDTheme.typography.subheading1,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(Modifier.weight(1f))
                TDText(
                    fullText = "$taskAmount This Week",
                    spanText = "$taskAmount",
                    spanStyle =
                        SpanStyle(
                            color = TDTheme.colors.black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                        ),
                    style = TDTheme.typography.subheading2,
                    color = TDTheme.colors.statusCardGray,
                )
                Spacer(Modifier.weight(0.6f))
            }
        }
    }
}

@Preview
@Composable
private fun CheckInBoxComponent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .background(TDTheme.colors.lightPurple)
                .padding(4.dp),
    ) {
        Icon(
            modifier = Modifier.size(13.dp),
            painter = painterResource(R.drawable.ic_check_svg),
            contentDescription = "check",
            tint = TDTheme.colors.darkPurple,
        )
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_rectangle_svg),
            contentDescription = "rectangle",
            tint = TDTheme.colors.darkPurple,
        )
    }
}

@Preview
@Composable
private fun CrossInBoxComponent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .background(TDTheme.colors.red)
                .padding(4.dp),
    ) {
        Icon(
            modifier = Modifier.size(13.dp),
            painter = painterResource(R.drawable.ic_back_slash),
            contentDescription = "check",
            tint = TDTheme.colors.crossRed,
        )
        Icon(
            modifier = Modifier.size(13.dp),
            painter = painterResource(R.drawable.ic_forward_slash),
            contentDescription = "check",
            tint = TDTheme.colors.crossRed,
        )
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(R.drawable.ic_rectangle_svg),
            contentDescription = "rectangle",
            tint = TDTheme.colors.crossRed,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TDStatisticCardCompletedPreview() {
    TDStatisticCard(
        "Task Complete",
        taskAmount = 10,
        isCompleted = true,
    )
}

@Preview(showBackground = true)
@Composable
private fun TDStatisticCardPendingPreview() {
    TDStatisticCard(
        "Task Pending",
        taskAmount = 3,
        isCompleted = false,
    )
}
