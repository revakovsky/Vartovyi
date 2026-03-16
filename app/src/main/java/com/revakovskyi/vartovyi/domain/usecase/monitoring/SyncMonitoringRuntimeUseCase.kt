package com.revakovskyi.vartovyi.domain.usecase.monitoring

import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

interface SyncMonitoringRuntimeUseCase {
    suspend operator fun invoke()
}

class SyncMonitoringRuntimeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
) : SyncMonitoringRuntimeUseCase {

    override suspend operator fun invoke() {
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

}

