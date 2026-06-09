package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

interface SeedDefaultStopWordsUseCase {
    suspend operator fun invoke()
}

class SeedDefaultStopWordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : SeedDefaultStopWordsUseCase {

    override suspend operator fun invoke() {
        keywordsRepository.seedDefaultStopWordsIfNeeded()
    }

}
