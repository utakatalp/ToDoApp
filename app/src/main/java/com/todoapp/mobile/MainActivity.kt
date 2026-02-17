package com.todoapp.mobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.todoapp.mobile.domain.repository.ThemeRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("No NavController provided")
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent(themeRepository = themeRepository)
        }
    }
}
