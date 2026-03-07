package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface AddStopWordUseCase {
    suspend operator fun invoke(stopWord: String)
}

class AddStopWordUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : AddStopWordUseCase {

    override suspend operator fun invoke(stopWord: String) {
        keywordsRepository.addStopWord(stopWord)
    }

}
