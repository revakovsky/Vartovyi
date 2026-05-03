package com.revakovskyi.vartovyi.usecase.monitoring

import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface ObserveMonitoringStateUseCase {
    operator fun invoke(): Flow<MonitoringState>
}

class ObserveMonitoringStateUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
) : ObserveMonitoringStateUseCase {

    override operator fun invoke(): Flow<MonitoringState> = combine(
        settingsRepository.isMonitoringActive,
        monitoringController.isMonitoringRunning,
        settingsRepository.isScheduleEnabled,
    ) { isMonitoringActive, isMonitoringRunning, isScheduleEnabled ->
        when {
            isMonitoringActive && isMonitoringRunning -> MonitoringState.ACTIVE
            isScheduleEnabled -> MonitoringState.SCHEDULED
            else -> MonitoringState.INACTIVE
        }
    }

}
