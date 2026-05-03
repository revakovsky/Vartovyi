package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

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
