package com.todoapp.mobile.domain.security

import androidx.fragment.app.FragmentActivity

interface Authenticator {
    suspend fun authenticate(activity: FragmentActivity): Boolean
}
