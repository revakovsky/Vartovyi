package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository

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
