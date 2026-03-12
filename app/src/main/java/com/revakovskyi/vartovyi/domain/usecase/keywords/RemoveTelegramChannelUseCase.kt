package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface RemoveTelegramChannelUseCase {
    suspend operator fun invoke(channel: String)
}

class RemoveTelegramChannelUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RemoveTelegramChannelUseCase {

    override suspend operator fun invoke(channel: String) {
        keywordsRepository.removeTelegramChannel(channel)
    }

}
