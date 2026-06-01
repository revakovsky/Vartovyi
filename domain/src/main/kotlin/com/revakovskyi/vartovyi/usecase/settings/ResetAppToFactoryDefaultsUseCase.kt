package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.usecase.monitoring.syncMonitoringRuntimeWithSettings

interface ResetAppToFactoryDefaultsUseCase {
    suspend operator fun invoke()
}

class ResetAppToFactoryDefaultsUseCaseImpl(
    private val alarmController: AlarmController,
    private val monitoringController: MonitoringController,
    private val settingsRepository: SettingsRepository,
    private val keywordsRepository: KeywordsRepository,
    private val logRepository: LogRepository,
) : ResetAppToFactoryDefaultsUseCase {

    override suspend fun invoke() {
        alarmController.stopAlarm()
        settingsRepository.clearAllMonitoringPreferences()
        keywordsRepository.clearAllKeywordsPreferences()
        keywordsRepository.restoreDefaultKeywords()
        keywordsRepository.restoreDefaultStopWords()
        logRepository.clearLog()
        syncMonitoringRuntimeWithSettings(
            settingsRepository = settingsRepository,
            monitoringController = monitoringController,
        )
    }

}
