package com.revakovskyi.vartovyi.domain.repository

import kotlinx.coroutines.flow.Flow

interface AlarmController {

    val isAlarmRunning: Flow<Boolean>

    fun triggerAlarm()
    fun stopAlarm()

}
