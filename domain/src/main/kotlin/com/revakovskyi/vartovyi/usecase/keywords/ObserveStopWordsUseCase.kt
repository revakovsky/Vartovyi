package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

interface ObserveStopWordsUseCase {
    operator fun invoke(): Flow<List<String>>
}

class ObserveStopWordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
) : ObserveStopWordsUseCase {

    override operator fun invoke(): Flow<List<String>> = keywordsRepository.stopWords

}
