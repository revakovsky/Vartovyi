package com.revakovskyi.vartovyi.domain.controllers.alarm

import kotlinx.coroutines.flow.Flow

interface AlarmController {

    val isAlarmRunning: Flow<Boolean>

    fun triggerAlarm(
        sourceChannelName: String = "",
        sourceMessageText: String = "",
    )

    fun stopAlarm()

}
