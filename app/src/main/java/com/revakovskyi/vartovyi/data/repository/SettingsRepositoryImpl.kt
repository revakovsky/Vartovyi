package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.datastore.MonitoringDataStore
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val monitoringDataStore: MonitoringDataStore,
) : SettingsRepository {

    override val isMonitoringActive: Flow<Boolean> = monitoringDataStore.isMonitoringActive
    override val isScheduleEnabled: Flow<Boolean> = monitoringDataStore.isScheduleEnabled
    override val startTime: Flow<String> = monitoringDataStore.startTime
    override val endTime: Flow<String> = monitoringDataStore.endTime
    override val alarmDurationSeconds: Flow<Int> = monitoringDataStore.alarmDurationSeconds
    override val alarmVolumePercent: Flow<Int> = monitoringDataStore.alarmVolumePercent
    override val alarmSoundUri: Flow<String> = monitoringDataStore.alarmSoundUri
    override val logSizeLimit: Flow<Int> = monitoringDataStore.logSizeLimit
    override val alarmRetriggerCooldownDurationMillis: Flow<Long> = monitoringDataStore.alarmRetriggerCooldownDurationMillis
    override val alarmRetriggerCooldownUntilEpochMillis: Flow<Long> = monitoringDataStore.alarmRetriggerCooldownUntilEpochMillis

    override suspend fun setMonitoringActive(active: Boolean) {
        monitoringDataStore.setMonitoringActive(active)
    }

    override suspend fun setScheduleEnabled(enabled: Boolean) {
        monitoringDataStore.setScheduleEnabled(enabled)
    }

    override suspend fun setStartTime(time: String) {
        monitoringDataStore.setStartTime(time)
    }

    override suspend fun setEndTime(time: String) {
        monitoringDataStore.setEndTime(time)
    }

    override suspend fun setAlarmDurationSeconds(seconds: Int) {
        monitoringDataStore.setAlarmDurationSeconds(seconds)
    }

    override suspend fun setAlarmVolumePercent(percent: Int) {
        monitoringDataStore.setAlarmVolumePercent(percent)
    }

    override suspend fun setAlarmSoundUri(uri: String) {
        monitoringDataStore.setAlarmSoundUri(uri)
    }

    override suspend fun setLogSizeLimit(limit: Int) {
        monitoringDataStore.setLogSizeLimit(limit)
    }

    override suspend fun setAlarmRetriggerCooldownDurationMillis(durationMillis: Long) {
        monitoringDataStore.setAlarmRetriggerCooldownDurationMillis(durationMillis)
    }

    override suspend fun setAlarmRetriggerCooldownUntilEpochMillis(untilEpochMillis: Long) {
        monitoringDataStore.setAlarmRetriggerCooldownUntilEpochMillis(untilEpochMillis)
    }

}
