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
        modifier =
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color = TDTheme.colors.infoCardBgColor)
            .padding(16.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            tint = TDTheme.colors.pendingGray,
        )
        Spacer(modifier = Modifier.width(12.dp))
        TDText(text = text, color = TDTheme.colors.pendingGray)
    }
}

@Preview(showBackground = true)
@Composable
private fun TDInfoCardPreview() {
    TDInfoCard(
        text =
        "You'll be able to invite your family members and " +
            "assign tasks to them immediately after creating " +
            "the group.",
        modifier = Modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun TDInfoCardShortPreview() {
    TDTheme {
        TDInfoCard(text = "Tap a task to edit it.", modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TDInfoCardLongPreview() {
    TDTheme {
        TDInfoCard(
            text =
            "Once a group is created, you can invite up to fifteen members. " +
                "Each member receives a notification and must accept the invitation " +
                "before they can see the group's shared tasks. Admins can revoke " +
                "access at any time from the group settings screen.",
            modifier = Modifier.padding(16.dp),
        )
    }
}
