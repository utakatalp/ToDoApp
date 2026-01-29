package com.todoapp.mobile.domain.security

import com.todoapp.mobile.data.security.SecretModeEndCondition
import java.time.Clock

class SecretModeConditionFactory(
    private val clock: Clock,
) {
    fun create(option: SecretModeReopenOption): SecretModeEndCondition {
        val now = clock.millis()

        return when (option) {
            is SecretModeReopenOption.Time ->
                SecretModeEndCondition.UntilTime(now + option.delay.toMillis())

            is SecretModeReopenOption.Event ->
                SecretModeEndCondition.UntilEvent(option.event)
        }
    }
}
