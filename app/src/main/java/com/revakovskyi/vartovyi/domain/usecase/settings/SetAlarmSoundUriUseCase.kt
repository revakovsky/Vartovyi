package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

interface SetAlarmSoundUriUseCase {
    suspend operator fun invoke(uri: String)
}

class SetAlarmSoundUriUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : SetAlarmSoundUriUseCase {

    override suspend operator fun invoke(uri: String) {
        settingsRepository.setAlarmSoundUri(uri)
    }

}

