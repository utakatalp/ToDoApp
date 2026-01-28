package com.todoapp.mobile.domain.usecase.security

import com.todoapp.mobile.domain.repository.SecretPreferences

class IsSecretModeActiveUseCase(
    private val secretPreferences: SecretPreferences,
    private val clock: java.time.Clock,
) {
    suspend operator fun invoke(): Boolean {
        val condition = secretPreferences.getCondition()
        return condition.isActive(clock.millis())
    }
}
