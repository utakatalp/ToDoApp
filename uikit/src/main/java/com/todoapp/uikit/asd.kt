package com.todoapp.uikit

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Asd(){

    Button(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth(0.7f)
            .height(88.dp),
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color.Gray
        )
    ) {
        Text(
            "Pause",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )
    }
}