package com.todoapp.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean

val LocalNavController =
    staticCompositionLocalOf<NavHostController> {
        error("No NavController provided")
    }

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { mainViewModel.isLoggedIn == null }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel.onPushIntent(intent)
        setContent {
            MainContent()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mainViewModel.onPushIntent(intent)
    }

    companion object {
        val suppressNextTransition = AtomicBoolean(false)
    }
}
