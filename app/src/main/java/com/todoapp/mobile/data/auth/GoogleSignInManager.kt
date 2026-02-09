package com.todoapp.mobile.data.auth

import android.content.Context
import android.util.Log
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

    suspend fun getGoogleIdToken(): Result<String> {
        return try {
            Log.d("GoogleSignIn", "Starting Google Sign-In...")

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(false)
                .build()

            Log.d("GoogleSignIn", "Client ID: ${context.getString(R.string.default_web_client_id)}")

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            when (val credential = result.credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        Log.d("GoogleSignIn", "Token alındı başarıyla")
                        Result.success(googleCredential.idToken)
                    } else {
                        Log.e("GoogleSignIn", "Beklenmeyen credential tipi: ${credential.type}")
                        Result.failure(Exception("Beklenmeyen credential tipi"))
                    }
                }

                else -> {
                    Log.e("GoogleSignIn", "Geçersiz credential")
                    Result.failure(Exception("Geçersiz credential"))
                }
            }
        } catch (e: GetCredentialException) {
            Log.e("GoogleSignIn", "GetCredentialException: ${e.message}", e)
            Result.failure(Exception("Google Sign-In iptal edildi: ${e.type} - ${e.message}"))
        } catch (e: IOException) {
            Log.e("GoogleSignIn", "Exception: ${e.message}", e)
            Result.failure(e)
        }
    }
}
