package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDIconWithText(icon: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(25))
            .background(color = TDTheme.colors.white)
            .border(1.dp, TDTheme.colors.lightGray, RoundedCornerShape(25))
            .padding(2.dp)
            .size(width = 80.dp, height = 32.dp)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(18.dp, 16.dp),
            painter = painterResource(icon),
            contentDescription = text,
            tint = TDTheme.colors.lightGray
        )
        Spacer(Modifier.width(4.dp))
        TDText(
            text = text,
            color = TDTheme.colors.lightGray,
            style = TDTheme.typography.cardText.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        ),
            overflow = TextOverflow.Visible
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF11111)
@Composable
private fun TDIconWithTextPreview() {
    TDTheme {
        TDIconWithText(
            icon = R.drawable.ic_filter,
            text = "Filter",
            modifier = Modifier
        )
    }
}
