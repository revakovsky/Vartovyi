package com.revakovskyi.vartovyi.usecase.monitoring

import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.flow.first

internal suspend fun syncMonitoringRuntimeWithSettings(
    settingsRepository: SettingsRepository,
    monitoringController: MonitoringController,
) {
    val isMonitoringActive = settingsRepository.isMonitoringActive.first()
    val isMonitoringRunning = monitoringController.isMonitoringRunning.first()

    if (isMonitoringActive && !isMonitoringRunning) {
        monitoringController.startMonitoring()
        return
    }

    if (!isMonitoringActive && isMonitoringRunning) {
        monitoringController.stopMonitoring()
    }
}
