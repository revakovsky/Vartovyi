package com.revakovskyi.vartovyi.usecase.alarm

import com.revakovskyi.vartovyi.controllers.alarm.AlarmRetriggerCooldownStateHolder
import kotlinx.coroutines.flow.Flow

interface ObserveAlarmRetriggerCooldownUseCase {
    operator fun invoke(): Flow<Long>
}

class ObserveAlarmRetriggerCooldownUseCaseImpl(
    private val alarmRetriggerCooldownStateHolder: AlarmRetriggerCooldownStateHolder,
) : ObserveAlarmRetriggerCooldownUseCase {

    override fun invoke(): Flow<Long> {
        return alarmRetriggerCooldownStateHolder.remainingCooldownMillis
    }

}
