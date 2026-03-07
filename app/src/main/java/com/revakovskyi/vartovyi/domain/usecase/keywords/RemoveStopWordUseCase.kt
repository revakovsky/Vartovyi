package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface RemoveStopWordUseCase {
    suspend operator fun invoke(stopWord: String)
}

class RemoveStopWordUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RemoveStopWordUseCase {

    override suspend operator fun invoke(stopWord: String) {
        keywordsRepository.removeStopWord(stopWord)
    }

}
