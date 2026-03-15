package com.revakovskyi.vartovyi.domain.usecase.monitoring

import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.domain.repository.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
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
