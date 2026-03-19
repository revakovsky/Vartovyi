package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetAlarmVolumeUseCase {
    suspend operator fun invoke(percent: Int)
}

class SetAlarmVolumeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetAlarmVolumeUseCase {

    override suspend operator fun invoke(percent: Int) {
        settingsRepository.setAlarmVolumePercent(percent)
    }

}
