package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

interface RestoreDefaultStopWordsUseCase {
    suspend operator fun invoke(): Int
}

class RestoreDefaultStopWordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RestoreDefaultStopWordsUseCase {

    override suspend operator fun invoke(): Int {
        return keywordsRepository.restoreDefaultStopWords()
    }

}
