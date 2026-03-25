package com.revakovskyi.vartovyi.usecase.emergency

import com.revakovskyi.vartovyi.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.repository.SettingsRepository

interface StopEverythingUseCase {
    suspend operator fun invoke()
}

class StopEverythingUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val monitoringController: MonitoringController,
    private val alarmController: AlarmController,
) : StopEverythingUseCase {

    override suspend operator fun invoke() {
        alarmController.stopAlarm()
        settingsRepository.setAlarmRetriggerCooldownUntilEpochMillis(0L)
        settingsRepository.setMonitoringActive(false)
        monitoringController.stopMonitoring()
    }

}
