package com.todoapp.mobile

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.fragment.app.FragmentActivity
import com.todoapp.mobile.data.auth.GoogleSignInManager
import com.todoapp.mobile.di.LocalGoogleSignInManager
import com.todoapp.mobile.domain.repository.ThemeRepository
import com.todoapp.mobile.navigation.ThemedApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    @Inject
    lateinit var googleSignInManager: GoogleSignInManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalGoogleSignInManager provides googleSignInManager) {
                ThemedApp(themeRepository = themeRepository)
            }
        }
    }
}
