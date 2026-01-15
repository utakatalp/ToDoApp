package com.todoapp.mobile.data.security.biometric

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.todoapp.mobile.R
import com.todoapp.mobile.domain.security.Authenticator
import javax.inject.Inject

class BiometricAuthenticator @Inject constructor() : Authenticator {
    override fun authenticate(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val authCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, authCallback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_authentication))
            .setSubtitle(activity.getString(R.string.log_in_using_your_biometric_credential))
            .setNegativeButtonText(activity.getString(R.string.cancel))
            .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
