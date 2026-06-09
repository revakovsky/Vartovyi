package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

interface SeedDefaultKeywordsUseCase {
    suspend operator fun invoke()
}

class SeedDefaultKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : SeedDefaultKeywordsUseCase {

    override suspend operator fun invoke() {
        keywordsRepository.seedDefaultKeywordsIfNeeded()
    }

}
