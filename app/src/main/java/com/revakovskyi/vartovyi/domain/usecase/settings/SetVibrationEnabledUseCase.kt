package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetVibrationEnabledUseCase {
    suspend operator fun invoke(enabled: Boolean)
}

class SetVibrationEnabledUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetVibrationEnabledUseCase {

    override suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setVibrationEnabled(enabled)
    }

}
