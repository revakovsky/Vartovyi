package com.revakovskyi.vartovyi.domain.usecase.log

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

interface ObserveLogEntriesUseCase {
    operator fun invoke(): Flow<List<AlertEvent>>
}

class ObserveLogEntriesUseCaseImpl(
    private val logRepository: LogRepository,
) : ObserveLogEntriesUseCase {

    override operator fun invoke(): Flow<List<AlertEvent>> = logRepository.logEntries

}
