package com.revakovskyi.vartovyi.domain.usecase.settings

import com.revakovskyi.vartovyi.domain.model.ScheduleSettings
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val DEFAULT_ALARM_VOLUME_PERCENT = 100
private const val DEFAULT_ALARM_SOUND_URI = ""

interface ObserveScheduleSettingsUseCase {
    operator fun invoke(): Flow<ScheduleSettings>
}

class ObserveScheduleSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ObserveScheduleSettingsUseCase {

    override operator fun invoke(): Flow<ScheduleSettings> {
        val baseScheduleSettingsFlow = combine(
            settingsRepository.isScheduleEnabled,
            settingsRepository.startTime,
            settingsRepository.endTime,
            settingsRepository.alarmDurationSeconds,
        ) { isScheduleEnabled, startTime, endTime, alarmDurationSeconds ->
            ScheduleSettings(
                isScheduleEnabled = isScheduleEnabled,
                startTime = startTime,
                endTime = endTime,
                alarmDurationSeconds = alarmDurationSeconds,
                alarmVolumePercent = DEFAULT_ALARM_VOLUME_PERCENT,
                alarmSoundUri = DEFAULT_ALARM_SOUND_URI,
            )
        }

        return combine(
            baseScheduleSettingsFlow,
            settingsRepository.alarmVolumePercent,
            settingsRepository.alarmSoundUri,
        ) { baseScheduleSettings, alarmVolumePercent, alarmSoundUri ->
            baseScheduleSettings.copy(
                alarmVolumePercent = alarmVolumePercent,
                alarmSoundUri = alarmSoundUri,
            )
        }
    }

}
