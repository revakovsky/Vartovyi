package com.revakovskyi.vartovyi.domain.usecase.alarm

import com.revakovskyi.vartovyi.domain.controllers.alarm.AlarmController
import kotlinx.coroutines.flow.Flow

interface ObserveAlarmRunningUseCase {
    operator fun invoke(): Flow<Boolean>
}

class ObserveAlarmRunningUseCaseImpl(
    private val alarmController: AlarmController,
) : ObserveAlarmRunningUseCase {

    override operator fun invoke(): Flow<Boolean> = alarmController.isAlarmRunning

}
