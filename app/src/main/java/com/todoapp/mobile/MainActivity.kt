package com.todoapp.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.todoapp.mobile.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                println(
                    "TESTTTTTTT DE TEKTTT",
                )
                println("asdşasdasd"
                )
                val alp = "asdşasdasd          "

            }
        }
    }
}
fun test() {
    val unused = 12551231236
}
