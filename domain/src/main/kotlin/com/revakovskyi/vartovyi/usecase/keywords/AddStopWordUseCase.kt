package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

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
