package com.todoapp.mobile.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.todoapp.mobile.R
import com.todoapp.mobile.data.ai.ChatToolRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    private const val MODEL_NAME = "gemini-2.5-flash"

    @Provides
    @Singleton
    fun provideGenerativeModel(
        @ApplicationContext context: Context,
        toolRegistry: ChatToolRegistry,
    ): GenerativeModel = Firebase.ai(backend = GenerativeBackend.vertexAI()).generativeModel(
        modelName = MODEL_NAME,
        systemInstruction = content { text(context.getString(R.string.chat_system_instruction)) },
        generationConfig = generationConfig {
            temperature = 0.2f
            maxOutputTokens = 1024
        },
        tools = listOf(Tool.functionDeclarations(toolRegistry.declarations)),
    )
}
