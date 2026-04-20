package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.LanguagePreference
import kotlinx.coroutines.flow.Flow

interface LanguageRepository {
    val languageFlow: Flow<LanguagePreference>

    suspend fun saveLanguage(language: LanguagePreference)

    suspend fun getCurrentLanguage(): LanguagePreference
}
