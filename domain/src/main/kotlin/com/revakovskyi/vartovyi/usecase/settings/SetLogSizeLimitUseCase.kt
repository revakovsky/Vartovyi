package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository

interface SetLogSizeLimitUseCase {
    suspend operator fun invoke(limit: Int)
}

class SetLogSizeLimitUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetLogSizeLimitUseCase {

    override suspend operator fun invoke(limit: Int) {
        settingsRepository.setLogSizeLimit(limit)
    }

}
