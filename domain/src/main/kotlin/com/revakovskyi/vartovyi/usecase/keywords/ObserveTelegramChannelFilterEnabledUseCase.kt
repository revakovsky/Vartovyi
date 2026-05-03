package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
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
