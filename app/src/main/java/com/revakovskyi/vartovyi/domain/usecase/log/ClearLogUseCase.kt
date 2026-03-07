package com.revakovskyi.vartovyi.domain.usecase.log

import com.revakovskyi.vartovyi.domain.repository.LogRepository

interface ClearLogUseCase {
    suspend operator fun invoke()
}

class ClearLogUseCaseImpl(
    private val logRepository: LogRepository,
) : ClearLogUseCase {

    override suspend operator fun invoke() {
        logRepository.clearLog()
    }

}
