package com.revakovskyi.vartovyi.domain.usecase.monitoring

import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface ObserveMonitoringStateUseCase {
    operator fun invoke(): Flow<MonitoringState>
}

class ObserveMonitoringStateUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveMonitoringStateUseCase {

    override operator fun invoke(): Flow<MonitoringState> = combine(
        settingsRepository.isMonitoringActive,
        settingsRepository.isScheduleEnabled,
    ) { isActive, isScheduleEnabled ->
        when {
            isActive -> MonitoringState.ACTIVE
            isScheduleEnabled -> MonitoringState.SCHEDULED
            else -> MonitoringState.INACTIVE
        }
    }

}
