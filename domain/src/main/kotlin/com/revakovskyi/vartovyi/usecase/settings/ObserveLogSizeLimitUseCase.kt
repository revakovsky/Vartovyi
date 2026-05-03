package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveLogSizeLimitUseCase {
    operator fun invoke(): Flow<Int>
}

class ObserveLogSizeLimitUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveLogSizeLimitUseCase {

    override operator fun invoke(): Flow<Int> = settingsRepository.logSizeLimit

}
