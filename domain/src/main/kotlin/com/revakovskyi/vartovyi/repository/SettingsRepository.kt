package com.revakovskyi.vartovyi.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val isMonitoringActive: Flow<Boolean>
    val isScheduleEnabled: Flow<Boolean>
    val startTime: Flow<String>
    val endTime: Flow<String>
    val alarmDurationSeconds: Flow<Int>
    val alarmVolumePercent: Flow<Int>
    val alarmSoundUri: Flow<String>
    val logSizeLimit: Flow<Int>
    val alarmRetriggerCooldownDurationMillis: Flow<Long>
    val alarmRetriggerCooldownUntilElapsedRealtimeMillis: Flow<Long>

    suspend fun setMonitoringActive(active: Boolean)
    suspend fun setScheduleEnabled(enabled: Boolean)
    suspend fun setStartTime(time: String)
    suspend fun setEndTime(time: String)
    suspend fun setAlarmDurationSeconds(seconds: Int)
    suspend fun setAlarmVolumePercent(percent: Int)
    suspend fun setAlarmSoundUri(uri: String)
    suspend fun setLogSizeLimit(limit: Int)
    suspend fun setAlarmRetriggerCooldownDurationMillis(durationMillis: Long)
    suspend fun setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(untilElapsedRealtimeMillis: Long)
    suspend fun clearAllMonitoringPreferences()

}
