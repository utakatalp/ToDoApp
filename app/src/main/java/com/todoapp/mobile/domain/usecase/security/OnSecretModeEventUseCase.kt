package com.todoapp.mobile.domain.usecase.security

import com.todoapp.mobile.data.security.SecretModeEndCondition
import com.todoapp.mobile.domain.repository.SecretPreferences
import com.todoapp.mobile.domain.security.SecretModeEndEvent

class OnSecretModeEventUseCase(
    private val secretPreferences: SecretPreferences
) {
    suspend operator fun invoke(event: SecretModeEndEvent) {
        val condition = secretPreferences.getCondition()
        if (condition is SecretModeEndCondition.UntilEvent && condition.event == event) {
            secretPreferences.saveCondition(SecretModeEndCondition.Disabled)
        }
    }
}
