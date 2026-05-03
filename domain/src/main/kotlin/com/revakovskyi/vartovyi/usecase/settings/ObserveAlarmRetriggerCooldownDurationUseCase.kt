package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveAlarmRetriggerCooldownDurationUseCase {
    operator fun invoke(): Flow<Long>
}

class ObserveAlarmRetriggerCooldownDurationUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveAlarmRetriggerCooldownDurationUseCase {

    override operator fun invoke(): Flow<Long> {
        return settingsRepository.alarmRetriggerCooldownDurationMillis
    }

}
