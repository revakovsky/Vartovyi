package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface AddTelegramChannelUseCase {
    suspend operator fun invoke(channel: String)
}

class AddTelegramChannelUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : AddTelegramChannelUseCase {

    override suspend operator fun invoke(channel: String) {
        keywordsRepository.addTelegramChannel(channel)
    }

}
