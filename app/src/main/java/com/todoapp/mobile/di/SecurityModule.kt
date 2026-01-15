package com.todoapp.mobile.di

import com.todoapp.mobile.data.security.biometric.BiometricAuthenticator
import com.todoapp.mobile.domain.security.Authenticator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindBiometricAuthenticator(
        impl: BiometricAuthenticator
    ): Authenticator
}
