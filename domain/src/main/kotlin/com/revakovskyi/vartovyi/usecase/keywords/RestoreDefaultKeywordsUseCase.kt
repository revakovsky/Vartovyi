package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

interface RestoreDefaultKeywordsUseCase {
    suspend operator fun invoke(): Int
}

class RestoreDefaultKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RestoreDefaultKeywordsUseCase {

    override suspend operator fun invoke(): Int {
        return keywordsRepository.restoreDefaultKeywords()
    }

}
