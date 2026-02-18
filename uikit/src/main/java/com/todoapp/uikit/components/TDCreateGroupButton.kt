package com.todoapp.uikit.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDCreateGroupButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    shape: RoundedCornerShape,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = TDTheme.colors.primary,
            contentColor = TDTheme.colors.white
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        TDText(
            text = text,
            color = TDTheme.colors.white,
            style = TDTheme.typography.regularTextStyle

        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TDCreateGroupButtonPreview() {
    TDTheme {
        TDCreateGroupButton(
            onClick = {},
            text = stringResource(R.string.create_new_group),
            shape = RoundedCornerShape(50),
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun TDCreateGroupButtonDarkPreview() {
    TDTheme {
        TDCreateGroupButton(
            onClick = {},
            text = stringResource(R.string.create_new_group),
            shape = RoundedCornerShape(50),
        )
    }
}
