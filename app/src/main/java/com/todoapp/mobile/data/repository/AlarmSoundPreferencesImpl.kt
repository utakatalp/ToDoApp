package com.todoapp.mobile.data.repository

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.todoapp.mobile.domain.repository.AlarmSoundOption
import com.todoapp.mobile.domain.repository.AlarmSoundPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmSoundPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreHelper: DataStoreHelper,
) : AlarmSoundPreferences {
    private val defaultAlarmUri: Uri
        get() = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ?: Uri.EMPTY

    override fun observeAlarmSoundUri(): Flow<Uri> = dataStoreHelper.observeOptionalString(KEY).map { value ->
        value?.let { runCatching { Uri.parse(it) }.getOrNull() } ?: defaultAlarmUri
    }

    override suspend fun saveAlarmSoundUri(uri: Uri) {
        dataStoreHelper.saveString(KEY, uri.toString())
    }

    override suspend fun currentAlarmSoundUri(): Uri = observeAlarmSoundUri().first()

    override fun systemAlarmSounds(): List<AlarmSoundOption> {
        val manager = RingtoneManager(context).apply { setType(RingtoneManager.TYPE_ALARM) }
        val cursor = manager.cursor
        val results = mutableListOf<AlarmSoundOption>()
        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val uri = manager.getRingtoneUri(cursor.position)
                if (title != null && uri != null) {
                    results.add(AlarmSoundOption(title = title, uri = uri))
                }
            } while (cursor.moveToNext())
        }
        runCatching { cursor.close() }
        return results
    }

    private companion object {
        const val KEY = "alarm_sound_uri"
    }
}
