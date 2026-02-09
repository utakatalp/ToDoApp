package com.todoapp.mobile.common.passwordValidation

object ValidationManager {

    object ValidationErrors {
        const val EMAIL_BLANK = "error_email_blank"
        const val EMAIL_INVALID = "error_email_invalid"
        const val PASSWORD_BLANK = "error_password_blank"
        const val PASSWORD_MIN_LENGTH = "error_password_min_length"
    }

    object PasswordRules {
        const val MIN_LENGTH = 8
        const val MEDIUM_THRESHOLD_LENGTH = 8
        const val STRONG_THRESHOLD_LENGTH = 12

        const val SCORE_LENGTH_STRONG = 2
        const val SCORE_LENGTH_MEDIUM = 1
        const val SCORE_LENGTH_NONE = 0

        const val SCORE_THRESHOLD_STRONG = 5
        const val SCORE_THRESHOLD_MEDIUM = 3
    }

    private fun runRules(vararg rules: () -> String): String {
        for (rule in rules) {
            val error = rule()
            if (error.isNotBlank()) return error
        }
        return ""
    }

    fun validateEmail(email: String): String = runRules(
        { if (email.isBlank()) ValidationErrors.EMAIL_BLANK else "" },
        {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                ValidationErrors.EMAIL_INVALID
            } else {
                ""
            }
        }
    )

    fun validatePassword(password: String): String = runRules(
        { if (password.isBlank()) ValidationErrors.PASSWORD_BLANK else "" },
        {
            if (password.length < PasswordRules.MIN_LENGTH) {
                ValidationErrors.PASSWORD_MIN_LENGTH
            } else {
                ""
            }
        }
    )

    fun computePasswordStrength(password: String): PasswordStrength? {
        if (password.isBlank()) return null

        var score = 0

        score += when {
            password.length >= PasswordRules.STRONG_THRESHOLD_LENGTH -> PasswordRules.SCORE_LENGTH_STRONG
            password.length >= PasswordRules.MEDIUM_THRESHOLD_LENGTH -> PasswordRules.SCORE_LENGTH_MEDIUM
            else -> PasswordRules.SCORE_LENGTH_NONE
        }

        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return when {
            score >= PasswordRules.SCORE_THRESHOLD_STRONG -> PasswordStrength.STRONG
            score >= PasswordRules.SCORE_THRESHOLD_MEDIUM -> PasswordStrength.MEDIUM
            else -> PasswordStrength.WEAK
        }
    }
}

enum class PasswordStrength { STRONG, MEDIUM, WEAK }
