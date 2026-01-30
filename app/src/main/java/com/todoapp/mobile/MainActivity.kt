package com.todoapp.mobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.navigation.ThemedApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.todoapp.mobile.navigation.ToDoApp
import com.todoapp.uikit.theme.TDTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThemedApp(themeRepository = themeRepository)
        }
    }
}
