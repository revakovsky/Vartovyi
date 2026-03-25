package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.repository.SettingsRepository

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

