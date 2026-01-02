package com.todoapp.uikit.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uikit.R
import com.todoapp.uikit.theme.TDTheme

@Composable
fun TDTaskCardWithCheckbox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    taskText: String,
    onCheckBoxClick: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TDCheckBox(isChecked = isChecked, onCheckBoxClick = onCheckBoxClick)
            Spacer(Modifier.weight(0.15f))
            TDText(
                text = taskText,
                style =
                    TDTheme.typography.regularTextStyle.copy(
                        textDecoration =
                            if (isChecked) {
                                TextDecoration.LineThrough
                            } else {
                                TextDecoration.None
                            },
                    ),
            )
            Spacer(Modifier.weight(2f))
            Icon(
                painter = painterResource(R.drawable.ic_drag),
                contentDescription = "drag",
                tint = TDTheme.colors.gray.copy(alpha = 0.4f),
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 32.dp),
            thickness = 1.dp,
            color = TDTheme.colors.gray.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun TDCheckBox(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckBoxClick: (Boolean) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .background(TDTheme.colors.lightGray)
                .clickable(
                    onClick = { onCheckBoxClick(isChecked) },
                ),
    ) {
        Icon(
            modifier =
                Modifier
                    .size(20.dp)
                    .border(width = 1.dp, color = TDTheme.colors.purple),
            painter = painterResource(id = R.drawable.ic_rectangle_sharp),
            contentDescription = "rectangle",
            tint = TDTheme.colors.white,
        )
        if (isChecked) {
            Icon(
                modifier = Modifier.size(10.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "check",
                tint = TDTheme.colors.purple,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TDTaskCardWithCheckboxPreview() {
    TDTaskCardWithCheckbox(
        isChecked = true,
        taskText = "Buy a cat food",
        onCheckBoxClick = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun TDTaskCardWithoutCheckboxPreview() {
    TDTaskCardWithCheckbox(
        isChecked = false,
        taskText = "Buy a cat food",
        onCheckBoxClick = { },
    )
}

@Preview
@Composable
private fun TDCheckBoxPreview() {
    TDCheckBox(modifier = Modifier, isChecked = true, onCheckBoxClick = { })
}

@Preview
@Composable
private fun TDCheckBoxPreviewWithoutCheck() {
    TDCheckBox(modifier = Modifier, isChecked = false, onCheckBoxClick = { })
}
