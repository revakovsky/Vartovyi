package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "vartovyi_monitoring"
private const val DEFAULT_START_TIME = "22:00"
private const val DEFAULT_END_TIME = "07:00"
private const val DEFAULT_TELEGRAM_PACKAGE = "org.telegram.messenger"
private const val DEFAULT_ALARM_RETRIGGER_COOLDOWN_MILLIS = 5 * 60 * 1000L

private val Context.monitoringDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

class MonitoringDataStore(private val context: Context) {

    private object Keys {
        val MONITORING_ACTIVE = booleanPreferencesKey("monitoring_active")
        val SCHEDULE_ENABLED = booleanPreferencesKey("schedule_enabled")
        val START_TIME = stringPreferencesKey("start_time")
        val END_TIME = stringPreferencesKey("end_time")
        val ALARM_DURATION_SECONDS = intPreferencesKey("alarm_duration_seconds")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val TELEGRAM_PACKAGES = stringSetPreferencesKey("telegram_packages")
        val LOG_SIZE_LIMIT = intPreferencesKey("log_size_limit")
        val ALARM_SOUND_URI = stringPreferencesKey("alarm_sound_uri")
        val ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS = longPreferencesKey("alarm_retrigger_cooldown_duration_millis")
        val ALARM_RETRIGGER_COOLDOWN_UNTIL_EPOCH_MILLIS = longPreferencesKey("alarm_retrigger_cooldown_until_epoch_millis")
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
        .map { it[Keys.ALARM_DURATION_SECONDS] ?: 60 }

    val isVibrationEnabled: Flow<Boolean> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.VIBRATION_ENABLED] ?: true }

    val selectedTelegramPackages: Flow<Set<String>> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.TELEGRAM_PACKAGES] ?: setOf(DEFAULT_TELEGRAM_PACKAGE) }

    val logSizeLimit: Flow<Int> = context.monitoringDataStore.data
        .safeCatch()
        .map { it[Keys.LOG_SIZE_LIMIT] ?: 300 }

    val alarmRetriggerCooldownDurationMillis: Flow<Long> = context.monitoringDataStore.data
        .safeCatch()
        .map { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS]
                ?: DEFAULT_ALARM_RETRIGGER_COOLDOWN_MILLIS
        }

    val alarmRetriggerCooldownUntilEpochMillis: Flow<Long> = context.monitoringDataStore.data
        .safeCatch()
        .map { preferences -> preferences[Keys.ALARM_RETRIGGER_COOLDOWN_UNTIL_EPOCH_MILLIS] ?: 0L }

    suspend fun setMonitoringActive(active: Boolean) {
        context.monitoringDataStore.edit { it[Keys.MONITORING_ACTIVE] = active }
    }

    suspend fun setScheduleEnabled(enabled: Boolean) {
        context.monitoringDataStore.edit { it[Keys.SCHEDULE_ENABLED] = enabled }
    }

    suspend fun setStartTime(time: String) {
        context.monitoringDataStore.edit { it[Keys.START_TIME] = time }
    }

    suspend fun setEndTime(time: String) {
        context.monitoringDataStore.edit { it[Keys.END_TIME] = time }
    }

    suspend fun setAlarmDurationSeconds(seconds: Int) {
        context.monitoringDataStore.edit { it[Keys.ALARM_DURATION_SECONDS] = seconds }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.monitoringDataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setSelectedTelegramPackages(packages: Set<String>) {
        context.monitoringDataStore.edit { it[Keys.TELEGRAM_PACKAGES] = packages }
    }

    suspend fun setLogSizeLimit(limit: Int) {
        context.monitoringDataStore.edit { it[Keys.LOG_SIZE_LIMIT] = limit }
    }

    suspend fun setAlarmRetriggerCooldownDurationMillis(durationMillis: Long) {
        context.monitoringDataStore.edit { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_DURATION_MILLIS] = durationMillis
        }
    }

    suspend fun setAlarmRetriggerCooldownUntilEpochMillis(untilEpochMillis: Long) {
        context.monitoringDataStore.edit { preferences ->
            preferences[Keys.ALARM_RETRIGGER_COOLDOWN_UNTIL_EPOCH_MILLIS] = untilEpochMillis
        }
    }

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

}
