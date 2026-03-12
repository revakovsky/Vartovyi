package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveTelegramChannelFilterEnabledUseCase {
    operator fun invoke(): Flow<Boolean>
}

class ObserveTelegramChannelFilterEnabledUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ObserveTelegramChannelFilterEnabledUseCase {

    override operator fun invoke(): Flow<Boolean> =
        keywordsRepository.isTelegramChannelFilterEnabled

}
