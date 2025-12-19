package com.todoapp.mobile.navigation

import kotlinx.serialization.Serializable

interface Screen {
    @Serializable
    data object Onboarding : Screen
}
