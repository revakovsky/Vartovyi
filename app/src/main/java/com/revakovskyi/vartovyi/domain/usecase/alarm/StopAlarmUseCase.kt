package com.revakovskyi.vartovyi.domain.usecase.alarm

import com.revakovskyi.vartovyi.domain.repository.AlarmController

interface StopAlarmUseCase {
    operator fun invoke()
}

class StopAlarmUseCaseImpl(
    private val alarmController: AlarmController,
) : StopAlarmUseCase {

    override operator fun invoke() {
        alarmController.stopAlarm()
    }

}
