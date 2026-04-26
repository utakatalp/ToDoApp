package com.todoapp.mobile.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

/** Title + URI for a single ringtone option that can be selected as the alarm sound. */
data class AlarmSoundOption(val title: String, val uri: Uri)

/**
 * Global user preference for the ringtone played when an alarm fires.
 * Backed by DataStore; default = system default alarm sound.
 */
interface AlarmSoundPreferences {
    fun observeAlarmSoundUri(): Flow<Uri>

    suspend fun saveAlarmSoundUri(uri: Uri)

    suspend fun currentAlarmSoundUri(): Uri

    /** Enumerates the device's built-in alarm-type ringtones for the picker screen. */
    fun systemAlarmSounds(): List<AlarmSoundOption>
}
