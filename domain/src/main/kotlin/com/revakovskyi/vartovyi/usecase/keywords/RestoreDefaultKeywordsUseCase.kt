package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.result.RestoreDefaultKeywordsResult

interface RestoreDefaultKeywordsUseCase {
    suspend operator fun invoke(): RestoreDefaultKeywordsResult
}

class RestoreDefaultKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RestoreDefaultKeywordsUseCase {

    override suspend operator fun invoke(): RestoreDefaultKeywordsResult {
        return keywordsRepository.restoreDefaultKeywords()
    }

}
