package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveKeywordsUseCase {
    operator fun invoke(): Flow<List<String>>
}

class ObserveKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ObserveKeywordsUseCase {

    override operator fun invoke(): Flow<List<String>> = keywordsRepository.keywords

}
