package com.todoapp.mobile.domain.security

import java.time.Duration

enum class SecretModeReopenOptionId {
    IMMEDIATE,
    MINUTES_1,
    MINUTES_3,
    MINUTES_5,
    MINUTES_10,
    MINUTES_15,
    UNTIL_APP_CLOSE,
}

sealed interface SecretModeReopenOption {
    val id: SecretModeReopenOptionId
    val label: String

    data class Time(
        override val id: SecretModeReopenOptionId,
        override val label: String,
        val delay: Duration,
    ) : SecretModeReopenOption

    data class Event(
        override val id: SecretModeReopenOptionId,
        override val label: String,
        val event: SecretModeEndEvent,
    ) : SecretModeReopenOption
}

@Suppress("MagicNumber")
object SecretModeReopenOptions {
    val Immediate = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.IMMEDIATE,
        label = "Now",
        delay = Duration.ZERO
    )

    val Minutes1 = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.MINUTES_1,
        label = "1 Minute",
        delay = Duration.ofMinutes(1)
    )

    val Minutes3 = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.MINUTES_3,
        label = "3 Minutes",
        delay = Duration.ofMinutes(3)
    )

    val Minutes5 = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.MINUTES_5,
        label = "5 Minutes",
        delay = Duration.ofMinutes(5)
    )

    val Minutes10 = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.MINUTES_10,
        label = "10 Minutes",
        delay = Duration.ofMinutes(10)
    )

    val Minutes15 = SecretModeReopenOption.Time(
        id = SecretModeReopenOptionId.MINUTES_15,
        label = "15 Minutes",
        delay = Duration.ofMinutes(15)
    )

    val UntilAppClose = SecretModeReopenOption.Event(
        id = SecretModeReopenOptionId.UNTIL_APP_CLOSE,
        label = "Until the app closed",
        event = SecretModeEndEvent.APP_CLOSED
    )

    val all = listOf(Immediate, Minutes1, Minutes3, Minutes5, Minutes10, Minutes15, UntilAppClose)

    fun byId(id: SecretModeReopenOptionId): SecretModeReopenOption =
        all.first { it.id == id }
}
