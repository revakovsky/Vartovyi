package com.revakovskyi.vartovyi.domain.usecase.log

import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.repository.LogRepository
import com.revakovskyi.vartovyi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first

interface AddLogEntryUseCase {
    suspend operator fun invoke(event: AlertEvent)
}

class AddLogEntryUseCaseImpl(
    private val logRepository: LogRepository,
    private val settingsRepository: SettingsRepository,
) : AddLogEntryUseCase {

    override suspend operator fun invoke(event: AlertEvent) {
        logRepository.addEntry(event)
        val sizeLimit = settingsRepository.logSizeLimit.first()
        logRepository.trimToLimit(sizeLimit)
    }

}
