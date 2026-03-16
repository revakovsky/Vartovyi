package com.revakovskyi.vartovyi.domain.usecase.alarm

import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController

interface TriggerAlarmUseCase {
    operator fun invoke(
        sourceChannelName: String = "",
        sourceMessageText: String = "",
    )
}

class TriggerAlarmUseCaseImpl(
    private val alarmController: AlarmController,
) : TriggerAlarmUseCase {

    override operator fun invoke(
        sourceChannelName: String,
        sourceMessageText: String,
    ) {
        alarmController.triggerAlarm(
            sourceChannelName = sourceChannelName,
            sourceMessageText = sourceMessageText,
        )
    }

}
