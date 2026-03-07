package com.revakovskyi.vartovyi.domain.usecase.monitoring

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface ToggleMonitoringUseCase {
    suspend operator fun invoke(isCurrentlyActive: Boolean)
}

class ToggleMonitoringUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ToggleMonitoringUseCase {

    override suspend operator fun invoke(isCurrentlyActive: Boolean) {
        settingsRepository.setMonitoringActive(!isCurrentlyActive)
    }

}
