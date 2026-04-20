package com.todoapp.mobile.data.security

import com.todoapp.mobile.domain.security.SecretModeEndEvent

sealed interface SecretModeEndCondition {
    fun isActive(nowMillis: Long): Boolean

    data class UntilTime(
        val epochMillis: Long,
    ) : SecretModeEndCondition {
        override fun isActive(nowMillis: Long): Boolean = nowMillis < epochMillis
    }

    data class UntilEvent(
        val event: SecretModeEndEvent,
    ) : SecretModeEndCondition {
        override fun isActive(nowMillis: Long): Boolean = true
    }

    data object Disabled : SecretModeEndCondition {
        override fun isActive(nowMillis: Long): Boolean = false
    }
}
