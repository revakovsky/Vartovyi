package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.model.ScheduleSettings
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface ObserveScheduleSettingsUseCase {
    operator fun invoke(): Flow<ScheduleSettings>
}

class ObserveScheduleSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveScheduleSettingsUseCase {

    override operator fun invoke(): Flow<ScheduleSettings> = combine(
        settingsRepository.isScheduleEnabled,
        settingsRepository.startTime,
        settingsRepository.endTime,
        settingsRepository.alarmDurationSeconds,
        settingsRepository.isVibrationEnabled,
    ) { isScheduleEnabled, startTime, endTime, alarmDurationSeconds, isVibrationEnabled ->
        ScheduleSettings(
            isScheduleEnabled = isScheduleEnabled,
            startTime = startTime,
            endTime = endTime,
            alarmDurationSeconds = alarmDurationSeconds,
            isVibrationEnabled = isVibrationEnabled,
        )
    }

}
