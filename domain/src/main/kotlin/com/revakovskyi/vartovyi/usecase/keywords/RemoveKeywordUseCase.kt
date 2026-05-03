package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository

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
