package com.revakovskyi.vartovyi.usecase.log

import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.repository.LogRepository
import kotlinx.coroutines.flow.Flow

interface ObserveLastAlarmTriggeredEventUseCase {
    operator fun invoke(): Flow<AlertEvent?>
}

class ObserveLastAlarmTriggeredEventUseCaseImpl(
    private val logRepository: LogRepository,
) : ObserveLastAlarmTriggeredEventUseCase {

    override fun invoke(): Flow<AlertEvent?> {
        return logRepository.lastAlarmTriggeredEvent
    }

}
