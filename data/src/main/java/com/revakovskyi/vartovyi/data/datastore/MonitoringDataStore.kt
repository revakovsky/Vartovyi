package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.revakovskyi.vartovyi.constants.SettingsDefaults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "vartovyi_monitoring"
private const val DEFAULT_START_TIME = "22:00"
private const val DEFAULT_END_TIME = "07:00"
private const val DEFAULT_ALARM_DURATION_SECONDS = 60
private const val DEFAULT_ALARM_VOLUME_PERCENT = 100
private const val DEFAULT_ALARM_SOUND_URI = ""
private const val DEFAULT_ALARM_RETRIGGER_COOLDOWN_MILLIS = 5 * 60 * 1000L

private val Context.monitoringDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    corruptionHandler = preferencesCorruptionHandler(),
)

internal class MonitoringDataStore(private val context: Context) {

    private object Keys {
        val MONITORING_ACTIVE = booleanPreferencesKey("monitoring_active")
        val SCHEDULE_ENABLED = booleanPreferencesKey("schedule_enabled")
        val START_TIME = stringPreferencesKey("start_time")
        val END_TIME = stringPreferencesKey("end_time")
        val ALARM_DURATION_SECONDS = intPreferencesKey("alarm_duration_seconds")
        val ALARM_VOLUME_PERCENT = intPreferencesKey("alarm_volume_percent")
        val LOG_SIZE_LIMIT = intPreferencesKey("log_size_limit")
        val ALARM_SOUND_URI = stringPreferencesKey("alarm_sound_uri")
        val ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS =
            longPreferencesKey("alarm_retrigger_cooldown_duration_millis")
        val ALARM_RETRIGGER_COOLDOWN_UNTIL_ELAPSED_REALTIME_MILLIS =
            longPreferencesKey("alarm_retrigger_cooldown_until_elapsed_realtime_millis")
    }

    val isMonitoringActive: Flow<Boolean> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.MONITORING_ACTIVE] ?: false }

    val isScheduleEnabled: Flow<Boolean> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.SCHEDULE_ENABLED] ?: false }

    val startTime: Flow<String> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.START_TIME] ?: DEFAULT_START_TIME }

    val endTime: Flow<String> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.END_TIME] ?: DEFAULT_END_TIME }

    val alarmDurationSeconds: Flow<Int> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.ALARM_DURATION_SECONDS] ?: DEFAULT_ALARM_DURATION_SECONDS }

    val alarmVolumePercent: Flow<Int> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.ALARM_VOLUME_PERCENT] ?: DEFAULT_ALARM_VOLUME_PERCENT }

    val alarmSoundUri: Flow<String> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.ALARM_SOUND_URI] ?: DEFAULT_ALARM_SOUND_URI }

    val logSizeLimit: Flow<Int> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.LOG_SIZE_LIMIT] ?: SettingsDefaults.LOG_SIZE_LIMIT }

    val alarmRetriggerCooldownDurationMillis: Flow<Long> = context.monitoringDataStore.data
        .safeCatch()
        .map { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS]
                ?: DEFAULT_ALARM_RETRIGGER_COOLDOWN_MILLIS
        }

    val alarmRetriggerCooldownUntilElapsedRealtimeMillis: Flow<Long> =
        context.monitoringDataStore.data
            .safeCatch()
            .map { preferences ->
                preferences[Keys.ALARM_RETRIGGER_COOLDOWN_UNTIL_ELAPSED_REALTIME_MILLIS] ?: 0L
            }

    suspend fun setMonitoringActive(active: Boolean): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.MONITORING_ACTIVE] = active }
    }

    suspend fun setScheduleEnabled(enabled: Boolean): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.SCHEDULE_ENABLED] = enabled }
    }

    suspend fun setStartTime(time: String): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.START_TIME] = time }
    }

    suspend fun setEndTime(time: String): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.END_TIME] = time }
    }

    suspend fun setAlarmDurationSeconds(seconds: Int): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.ALARM_DURATION_SECONDS] = seconds }
    }

    suspend fun setAlarmVolumePercent(percent: Int): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.ALARM_VOLUME_PERCENT] = percent }
    }

    suspend fun setAlarmSoundUri(uri: String): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.ALARM_SOUND_URI] = uri }
    }

    suspend fun setLogSizeLimit(limit: Int): Boolean {
        return context.monitoringDataStore.safeEdit { it[Keys.LOG_SIZE_LIMIT] = limit }
    }

    suspend fun setAlarmRetriggerCooldownDurationMillis(durationMillis: Long): Boolean {
        return context.monitoringDataStore.safeEdit { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS] = durationMillis
        }
    }

    suspend fun setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(untilElapsedRealtimeMillis: Long): Boolean {
        return context.monitoringDataStore.safeEdit { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_UNTIL_ELAPSED_REALTIME_MILLIS] =
                untilElapsedRealtimeMillis
        }
    }

    suspend fun clearAllPreferences(): Boolean {
        return context.monitoringDataStore.safeEdit { preferences ->
            preferences.clear()
        }
    }

}
