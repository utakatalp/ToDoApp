package com.todoapp.mobile.di

import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.data.source.remote.api.TodoAuthApi
import com.todoapp.mobile.data.source.remote.authenticator.TokenRefreshAuthenticator
import com.todoapp.mobile.data.source.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideTokenRefreshMutex(): Mutex = Mutex()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val json = Json {
            ignoreUnknownKeys = false
            isLenient = true
            encodeDefaults = true
        }
        return Retrofit.Builder()
            .baseUrl("https://api.candroid.dev/todos/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @Named("token")
    fun provideTokenRetrofit(): Retrofit {
        val json = Json {
            ignoreUnknownKeys = false
            isLenient = true
            encodeDefaults = true
        }
        return Retrofit.Builder()
            .baseUrl("https://api.candroid.dev/todos/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenRefreshAuthenticator: TokenRefreshAuthenticator
    ) =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenRefreshAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideTDApi(retrofit: Retrofit): ToDoApi = retrofit.create(ToDoApi::class.java)

    @Provides
    @Singleton
    fun provideTokenApi(@Named("token") retrofit: Retrofit): TodoAuthApi = retrofit.create(TodoAuthApi::class.java)
}
