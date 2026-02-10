package com.todoapp.mobile.di

import com.todoapp.mobile.data.source.remote.api.ToDoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
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
    fun provideTDApi(retrofit: Retrofit): ToDoApi = retrofit.create(ToDoApi::class.java)
}
