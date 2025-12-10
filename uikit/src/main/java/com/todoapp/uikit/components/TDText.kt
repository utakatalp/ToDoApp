package com.todoapp.uikit.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview



@Composable
fun TDText(
    text: String,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    Text(
        text = text,
        style = textStyle
    )
}

@Preview(showBackground = true)
@Composable
fun TDTextExample() {
    TDText(
        text = "This is a text.",
        textStyle = TextStyle(
        )
    )
}