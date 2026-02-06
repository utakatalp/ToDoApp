package com.todoapp.mobile.di

import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.data.source.remote.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
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
    fun provideOkHttpClient(authInterceptor: AuthInterceptor) =
        OkHttpClient.Builder().addInterceptor(authInterceptor).build()

    @Provides
    @Singleton
    fun provideTDApi(retrofit: Retrofit): ToDoApi = retrofit.create(ToDoApi::class.java)
}
