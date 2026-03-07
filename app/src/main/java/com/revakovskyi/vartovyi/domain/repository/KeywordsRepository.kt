package com.revakovskyi.vartovyi.domain.repository

import kotlinx.coroutines.flow.Flow

interface KeywordsRepository {

    val keywords: Flow<List<String>>
    val stopWords: Flow<List<String>>

    suspend fun addKeyword(keyword: String)
    suspend fun removeKeyword(keyword: String)
    suspend fun addStopWord(stopWord: String)
    suspend fun removeStopWord(stopWord: String)

}
