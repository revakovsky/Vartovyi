package com.revakovskyi.vartovyi.domain.usecase.alarm

import com.revakovskyi.vartovyi.domain.repository.AlarmController

interface TriggerAlarmUseCase {
    operator fun invoke(matchedKeyword: String = "")
}

class TriggerAlarmUseCaseImpl(
    private val alarmController: AlarmController,
) : TriggerAlarmUseCase {

    override operator fun invoke(matchedKeyword: String) {
        alarmController.triggerAlarm(matchedKeyword)
    }

}
