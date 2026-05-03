package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository

interface SetScheduleEnabledUseCase {
    suspend operator fun invoke(enabled: Boolean)
}

class SetScheduleEnabledUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetScheduleEnabledUseCase {

    override suspend operator fun invoke(enabled: Boolean) {
        settingsRepository.setScheduleEnabled(enabled)
    }

}
