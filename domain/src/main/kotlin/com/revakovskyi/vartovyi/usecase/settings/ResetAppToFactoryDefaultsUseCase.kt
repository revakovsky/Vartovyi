package com.revakovskyi.vartovyi.usecase.settings

import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.OnboardingRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.usecase.keywords.buildCitySeededKeywords
import com.revakovskyi.vartovyi.usecase.keywords.normalizeCityForSeed
import com.revakovskyi.vartovyi.usecase.monitoring.syncMonitoringRuntimeWithSettings
import kotlinx.coroutines.flow.first

interface ResetAppToFactoryDefaultsUseCase {
    suspend operator fun invoke()
}

class ResetAppToFactoryDefaultsUseCaseImpl(
    private val alarmController: AlarmController,
    private val monitoringController: MonitoringController,
    private val settingsRepository: SettingsRepository,
    private val keywordsRepository: KeywordsRepository,
    private val logRepository: LogRepository,
    private val onboardingRepository: OnboardingRepository,
) : ResetAppToFactoryDefaultsUseCase {

    override suspend fun invoke() {
        alarmController.stopAlarm()
        settingsRepository.clearAllMonitoringPreferences()
        keywordsRepository.clearAllKeywordsPreferences()

        val savedCity = onboardingRepository.onboardingCity.first()
        val seed = normalizeCityForSeed(savedCity)
            ?.let { normalizedCity -> buildCitySeededKeywords(normalizedCity) }
            ?: DEFAULT_KEYWORDS_SEED
        keywordsRepository.restoreDefaultKeywords(seed = seed)

        keywordsRepository.restoreDefaultStopWords()
        logRepository.clearLog()
        syncMonitoringRuntimeWithSettings(
            settingsRepository = settingsRepository,
            monitoringController = monitoringController,
        )
    }

}
