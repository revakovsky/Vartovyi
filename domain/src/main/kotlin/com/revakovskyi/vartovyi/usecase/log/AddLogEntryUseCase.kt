package com.revakovskyi.vartovyi.usecase.log

import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.repository.LogRepository
import com.revakovskyi.vartovyi.repository.SettingsRepository
import kotlinx.coroutines.flow.first

interface AddLogEntryUseCase {
    suspend operator fun invoke(event: AlertEvent)
}

class AddLogEntryUseCaseImpl(
    private val logRepository: LogRepository,
    private val settingsRepository: SettingsRepository,
) : AddLogEntryUseCase {

    override suspend operator fun invoke(event: AlertEvent) {
        val isInserted = logRepository.addEntry(event)
        if (!isInserted) return

        val sizeLimit = settingsRepository.logSizeLimit.first()
        logRepository.trimToLimit(sizeLimit)
    }

}
