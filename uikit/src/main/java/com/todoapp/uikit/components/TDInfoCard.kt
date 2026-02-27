package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDInfoCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = TDTheme.colors.infoCardBgColor)
            .padding(16.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_info), contentDescription = null, tint = Color.Unspecified)
        Spacer(modifier = Modifier.width(12.dp))
        TDText(text = text, color = TDTheme.colors.primary)
    }
}

@Preview(showBackground = true)
@Composable
private fun TDInfoCardPreview() {
    TDInfoCard(
        text = "You'll be able to invite your family members and " +
                "assign tasks to them immediately after creating " +
                "the group.",
        modifier = Modifier
    )
}
