package com.revakovskyi.vartovyi.domain.usecase.log

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.LogRepository
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
