package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetEndTimeUseCase {
    suspend operator fun invoke(time: String)
}

class SetEndTimeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetEndTimeUseCase {

    override suspend operator fun invoke(time: String) {
        settingsRepository.setEndTime(time)
    }

}
