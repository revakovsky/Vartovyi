package com.revakovskyi.vartovyi.domain.usecase.emergency

import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController
import com.revakovskyi.vartovyi.domain.controllers.notification_monitoring.MonitoringController
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository

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
        settingsRepository.setMonitoringActive(false)
        monitoringController.stopMonitoring()
    }

}
