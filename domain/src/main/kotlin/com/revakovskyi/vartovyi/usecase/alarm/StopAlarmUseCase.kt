package com.revakovskyi.vartovyi.usecase.alarm

import com.revakovskyi.vartovyi.controllers.alarm.AlarmController

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
