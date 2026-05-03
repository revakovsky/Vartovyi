package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.first

interface ToggleTelegramChannelFilterUseCase {
    suspend operator fun invoke()
}

class ToggleTelegramChannelFilterUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ToggleTelegramChannelFilterUseCase {

    override suspend operator fun invoke() {
        val current = keywordsRepository.isTelegramChannelFilterEnabled.first()
        keywordsRepository.setTelegramChannelFilterEnabled(!current)
    }

}
