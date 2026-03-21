package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetAlarmRetriggerCooldownDurationUseCase {
    suspend operator fun invoke(durationMillis: Long)
}

class SetAlarmRetriggerCooldownDurationUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetAlarmRetriggerCooldownDurationUseCase {

    override suspend operator fun invoke(durationMillis: Long) {
        settingsRepository.setAlarmRetriggerCooldownDurationMillis(durationMillis)
    }

}

