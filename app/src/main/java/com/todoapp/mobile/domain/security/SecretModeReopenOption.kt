package com.todoapp.mobile.domain.security

import java.time.Duration

@Suppress("MagicNumber")
enum class SecretModeReopenOption(
    val label: String,
    val delay: Duration?,
) {
    IMMEDIATE("Now", Duration.ZERO),
    MINUTES_1("1 Minute", Duration.ofMinutes(1)),
    MINUTES_3("3 Minutes", Duration.ofMinutes(3)),
    MINUTES_5("5 Minutes", Duration.ofMinutes(5)),
    MINUTES_10("10 Minutes", Duration.ofMinutes(10)),
    MINUTES_15("15 Minutes", Duration.ofMinutes(15)),
    UNTIL_APP_CLOSE("Until the app closed", null),
    ;
    companion object {
        fun fromStorage(value: String?): SecretModeReopenOption {
            return entries.firstOrNull { it.name == value } ?: IMMEDIATE
        }
    }
}
