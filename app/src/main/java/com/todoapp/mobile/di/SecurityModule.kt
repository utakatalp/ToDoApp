package com.todoapp.mobile.di

import com.todoapp.mobile.data.security.biometric.BiometricAuthenticator
import com.todoapp.mobile.domain.security.Authenticator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SecurityModule {

    @Binds
    @ViewModelScoped
    abstract fun bindBiometricAuthenticator(
        impl: BiometricAuthenticator
    ): Authenticator
}
