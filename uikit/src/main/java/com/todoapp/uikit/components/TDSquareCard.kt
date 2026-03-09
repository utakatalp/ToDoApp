package com.todoapp.uikit.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDSquareCard(
    numberColor: Color = TDTheme.colors.black,
    number: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .height(77.dp)
            .widthIn(min = 110.dp, max = 200.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = TDTheme.colors.white,
        ),
        border = BorderStroke(1.dp, TDTheme.colors.gray)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

        ) {
            TDText(text = number, color = numberColor, style = TDTheme.typography.number)
            TDText(text = text, style = TDTheme.typography.cardText, color = TDTheme.colors.lightGray)
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFF11AE2
)
@Composable
private fun TDSquareCardPreview() {
    TDTheme {
        TDSquareCard(
            number = "12",
            text = "COMPLETED",
            numberColor = TDTheme.colors.green
        )
    }
}
