package com.todoapp.mobile.data.repository

import android.content.Context
import androidx.core.content.edit
import com.todoapp.mobile.domain.model.LanguagePreference
import com.todoapp.mobile.domain.repository.LanguageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LanguageRepositoryImpl @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
    @ApplicationContext private val context: Context,
) : LanguageRepository {

    override val languageFlow: Flow<LanguagePreference> =
        dataStoreHelper.getString(LanguagePreference.PREFS_KEY, LanguagePreference.ENGLISH.name)
            .map { safeValueOf(it) }

    override suspend fun saveLanguage(language: LanguagePreference) {
        dataStoreHelper.saveString(LanguagePreference.PREFS_KEY, language.name)
        context.getSharedPreferences(LanguagePreference.PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(LanguagePreference.PREFS_KEY, language.name) }
    }

    override suspend fun getCurrentLanguage(): LanguagePreference {
        return dataStoreHelper.getString(LanguagePreference.PREFS_KEY, LanguagePreference.ENGLISH.name)
            .first()
            .let { safeValueOf(it) }
    }

    private fun safeValueOf(name: String): LanguagePreference =
        try {
            LanguagePreference.valueOf(name)
        } catch (e: IllegalArgumentException) {
            LanguagePreference.ENGLISH
        }
}
