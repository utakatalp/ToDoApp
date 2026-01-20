package com.todoapp.uikit.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDPickerField(
    title: String,
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier,
    ) {
        TDText(
            text = title,
            style = TDTheme.typography.regularTextStyle,
            color = TDTheme.colors.onBackground,
        )

        Spacer(Modifier.height(8.dp))
        Box(
            modifier =
                Modifier
                    .clickable(onClick = onClick),
        ) {
            TDCompactOutlinedTextField(
                enabled = false,
                label = "",
                value = value.orEmpty(),
                onValueChange = { },
                modifier =
                    Modifier
                        .fillMaxWidth(),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isError = isError,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TDPickerFieldErrorPreview() {
    TDTheme {
        TDPickerField(
            title = "Pick a date",
            value = "",
            onClick = {},
            isError = true,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar2),
                    contentDescription = null,
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                )
            }
        )
    }
}
