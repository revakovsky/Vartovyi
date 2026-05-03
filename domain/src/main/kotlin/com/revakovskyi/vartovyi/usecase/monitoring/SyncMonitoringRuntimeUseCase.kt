package com.revakovskyi.vartovyi.usecase.monitoring

import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository

interface SyncMonitoringRuntimeUseCase {
    suspend operator fun invoke()
}

class SyncMonitoringRuntimeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
) : SyncMonitoringRuntimeUseCase {

    override suspend operator fun invoke() {
        syncMonitoringRuntimeWithSettings(
            settingsRepository = settingsRepository,
            monitoringController = monitoringController,
        )
    }

}
