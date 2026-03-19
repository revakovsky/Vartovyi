package com.revakovskyi.vartovyi.domain.usecase.log

import androidx.paging.PagingData
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import kotlinx.coroutines.flow.Flow

interface ObserveLogEntriesUseCase {
    operator fun invoke(): Flow<PagingData<AlertEvent>>
}

class ObserveLogEntriesUseCaseImpl(
    private val logRepository: LogRepository,
) : ObserveLogEntriesUseCase {

    override operator fun invoke(): Flow<PagingData<AlertEvent>> = logRepository.logEntries

}
