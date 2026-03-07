package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository

interface AddKeywordUseCase {
    suspend operator fun invoke(keyword: String)
}

class AddKeywordUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : AddKeywordUseCase {

    override suspend operator fun invoke(keyword: String) {
        keywordsRepository.addKeyword(keyword)
    }

}
