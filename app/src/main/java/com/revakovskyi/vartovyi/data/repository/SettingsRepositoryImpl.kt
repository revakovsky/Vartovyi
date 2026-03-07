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
    override val isVibrationEnabled: Flow<Boolean> = monitoringDataStore.isVibrationEnabled
    override val selectedTelegramPackages: Flow<Set<String>> = monitoringDataStore.selectedTelegramPackages
    override val logSizeLimit: Flow<Int> = monitoringDataStore.logSizeLimit

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

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        monitoringDataStore.setVibrationEnabled(enabled)
    }

    override suspend fun setSelectedTelegramPackages(packages: Set<String>) {
        monitoringDataStore.setSelectedTelegramPackages(packages)
    }

    override suspend fun setLogSizeLimit(limit: Int) {
        monitoringDataStore.setLogSizeLimit(limit)
    }

}
