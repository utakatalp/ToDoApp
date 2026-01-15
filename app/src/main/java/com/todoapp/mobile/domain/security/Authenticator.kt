package com.todoapp.mobile.domain.security

import androidx.fragment.app.FragmentActivity

interface Authenticator {
    fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
    )
}
