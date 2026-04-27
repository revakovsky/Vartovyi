package com.revakovskyi.vartovyi.usecase.monitoring

import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository

interface ToggleMonitoringUseCase {
    suspend operator fun invoke(isCurrentlyActive: Boolean)
}

class ToggleMonitoringUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
) : ToggleMonitoringUseCase {

    override suspend operator fun invoke(isCurrentlyActive: Boolean) {
        val shouldActivate = !isCurrentlyActive
        settingsRepository.setMonitoringActive(shouldActivate)

        if (!shouldActivate) {
            settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(0L)
        }

        if (shouldActivate) monitoringController.startMonitoring()
        else monitoringController.stopMonitoring()
    }

}
