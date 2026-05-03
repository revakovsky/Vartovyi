package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import com.revakovskyi.vartovyi.usecase.monitoring.syncMonitoringRuntimeWithSettings

interface ClearKeywordsScreenDataUseCase {
    suspend operator fun invoke()
}

class ClearKeywordsScreenDataUseCaseImpl(
    private val alarmController: AlarmController,
    private val monitoringController: MonitoringController,
    private val settingsRepository: SettingsRepository,
    private val keywordsRepository: KeywordsRepository,
) : ClearKeywordsScreenDataUseCase {

    override suspend fun invoke() {
        alarmController.stopAlarm()
        settingsRepository.setMonitoringActive(false)
        settingsRepository.setAlarmRetriggerCooldownUntilElapsedRealtimeMillis(0L)
        keywordsRepository.clearAllKeywordsPreferences()
        syncMonitoringRuntimeWithSettings(
            settingsRepository = settingsRepository,
            monitoringController = monitoringController,
        )
    }

}
