package com.revakovskyi.vartovyi.domain.usecase.log

import com.revakovskyi.vartovyi.domain.repository.LogRepository

interface GetLogEntryIndexUseCase {
    suspend operator fun invoke(eventId: String): Int
}

class GetLogEntryIndexUseCaseImpl(
    private val logRepository: LogRepository,
) : GetLogEntryIndexUseCase {

    override suspend fun invoke(eventId: String): Int {
        return logRepository.getEntryIndexById(eventId = eventId)
    }

}
