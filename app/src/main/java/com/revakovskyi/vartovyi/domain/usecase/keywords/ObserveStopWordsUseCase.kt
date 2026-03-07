package com.revakovskyi.vartovyi.domain.usecase.keywords

import com.revakovskyi.vartovyi.domain.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveStopWordsUseCase {
    operator fun invoke(): Flow<List<String>>
}

class ObserveStopWordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ObserveStopWordsUseCase {

    override operator fun invoke(): Flow<List<String>> = keywordsRepository.stopWords

}
