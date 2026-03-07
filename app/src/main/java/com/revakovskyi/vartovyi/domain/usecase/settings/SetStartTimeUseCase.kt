package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetStartTimeUseCase {
    suspend operator fun invoke(time: String)
}

class SetStartTimeUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetStartTimeUseCase {

    override suspend operator fun invoke(time: String) {
        settingsRepository.setStartTime(time)
    }

}
