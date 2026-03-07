package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveTelegramPackagesUseCase {
    operator fun invoke(): Flow<Set<String>>
}

class ObserveTelegramPackagesUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveTelegramPackagesUseCase {

    override operator fun invoke(): Flow<Set<String>> = settingsRepository.selectedTelegramPackages

}
