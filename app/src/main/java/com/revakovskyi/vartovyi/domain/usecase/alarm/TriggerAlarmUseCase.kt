package com.revakovskyi.vartovyi.domain.usecase.alarm

import com.revakovskyi.vartovyi.domain.repository.AlarmController

interface TriggerAlarmUseCase {
    operator fun invoke()
}

class TriggerAlarmUseCaseImpl(
    private val alarmController: AlarmController,
) : TriggerAlarmUseCase {

    override operator fun invoke() {
        alarmController.triggerAlarm()
    }

}
