package com.todoapp.mobile.ui.common

import androidx.compose.runtime.compositionLocalOf

/**
 * Indicates whether the user has enabled the "Reduce motion" accessibility
 * setting (Settings → Accessibility → Reduce motion).
 *
 * Components that run looping or attention-grabbing animations should read
 * this and skip them when true. Provided at the activity level via the
 * MainActivity composition root, sourced from DataStore.
 */
val LocalReduceMotion = compositionLocalOf { false }
