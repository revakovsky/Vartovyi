package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface RemoveKeywordUseCase {
    suspend operator fun invoke(keyword: String)
}

class RemoveKeywordUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : RemoveKeywordUseCase {

    override suspend operator fun invoke(keyword: String) {
        keywordsRepository.removeKeyword(keyword)
    }

}
