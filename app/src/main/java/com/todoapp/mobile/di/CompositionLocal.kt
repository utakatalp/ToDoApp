package com.todoapp.mobile.di

import androidx.compose.runtime.compositionLocalOf
import com.todoapp.mobile.data.auth.GoogleSignInManager

val LocalGoogleSignInManager = compositionLocalOf<GoogleSignInManager> {
    error("GoogleSignInManager not provided")
}
