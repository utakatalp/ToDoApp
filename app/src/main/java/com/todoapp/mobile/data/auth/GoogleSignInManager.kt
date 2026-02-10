package com.todoapp.mobile.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.todoapp.mobile.R
import dagger.hilt.android.qualifiers.ApplicationContext
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun getGoogleIdToken(activityContext: Context): Result<String> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = activityContext
            )

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Result.success(googleCredential.idToken)
                    } else {
                        Result.failure(Exception("Unexpected credential type"))
                    }
                }

                else -> {
                    Result.failure(Exception("Invalid credential"))
                }
            }
        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In Cancelled: ${e.type} - ${e.message}"))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
