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
import androidx.compose.material3.IconButton
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
    onEditClick: () -> Unit = {},
) {
    Column(modifier.background(TDTheme.colors.background)) {
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
                color = TDTheme.colors.onBackground,
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

            IconButton(
                onClick = onEditClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit_task),
                    tint = TDTheme.colors.onBackground,
                    contentDescription = "edit",
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_drag),
                tint = TDTheme.colors.onBackground,
                contentDescription = "drag",
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
                .background(TDTheme.colors.onBackground)
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
            tint = TDTheme.colors.darkPurple,
        )
        if (isChecked) {
            Icon(
                modifier = Modifier.size(10.dp),
                painter = painterResource(id = R.drawable.ic_check),
                tint = TDTheme.colors.white,
                contentDescription = "check",
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
        onEditClick = { },
    )
}

@Preview(showBackground = true)
@Composable
private fun TDTaskCardWithoutCheckboxPreview() {
    TDTaskCardWithCheckbox(
        isChecked = false,
        taskText = "Buy a cat food",
        onCheckBoxClick = { },
        onEditClick = { },
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

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TDTaskCardWithCheckboxPreview_Dark() {
    TDTaskCardWithCheckbox(
        isChecked = true,
        taskText = "Buy a cat food",
        onCheckBoxClick = { },
        onEditClick = { },
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TDTaskCardWithoutCheckboxPreview_Dark() {
    TDTaskCardWithCheckbox(
        isChecked = false,
        taskText = "Buy a cat food",
        onCheckBoxClick = { },
        onEditClick = { },
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TDCheckBoxPreview_Dark() {
    TDCheckBox(modifier = Modifier, isChecked = true, onCheckBoxClick = { })
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TDCheckBoxPreviewWithoutCheck_Dark() {
    TDCheckBox(modifier = Modifier, isChecked = false, onCheckBoxClick = { })
}
