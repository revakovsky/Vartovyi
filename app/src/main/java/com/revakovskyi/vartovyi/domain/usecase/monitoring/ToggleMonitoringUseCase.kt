package com.revakovskyi.vartovyi.domain.usecase.monitoring

import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

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
            settingsRepository.setAlarmRetriggerCooldownUntilEpochMillis(0L)
        }

        if (shouldActivate) monitoringController.startMonitoring()
        else monitoringController.stopMonitoring()
    }

}
