package com.todoapp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.todoapp.mobile.theme.ToDoAppTheme
import com.todoapp.uikit.components.TDDatePickerPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                Scaffold {
                    Column(modifier = Modifier.padding(it).navi) {
                        TDDatePickerPreview()
                        Mo
                    }
                }
            }
        }
    }
}
