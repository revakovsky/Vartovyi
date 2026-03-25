package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveTelegramChannelsUseCase {
    operator fun invoke(): Flow<List<String>>
}

class ObserveTelegramChannelsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ObserveTelegramChannelsUseCase {

    override operator fun invoke(): Flow<List<String>> = keywordsRepository.telegramChannels

}
