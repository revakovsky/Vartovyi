package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository

interface SetAlarmDurationUseCase {
    suspend operator fun invoke(seconds: Int)
}

class SetAlarmDurationUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetAlarmDurationUseCase {

    override suspend operator fun invoke(seconds: Int) {
        settingsRepository.setAlarmDurationSeconds(seconds)
    }

}
