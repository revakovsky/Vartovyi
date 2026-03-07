package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetTelegramPackagesUseCase {
    suspend operator fun invoke(packages: Set<String>)
}

class SetTelegramPackagesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetTelegramPackagesUseCase {

    override suspend operator fun invoke(packages: Set<String>) {
        settingsRepository.setSelectedTelegramPackages(packages)
    }

}
