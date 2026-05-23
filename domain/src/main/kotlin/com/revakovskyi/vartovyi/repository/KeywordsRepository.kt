package com.revakovskyi.vartovyi.repository

import com.revakovskyi.vartovyi.result.RestoreDefaultKeywordsResult
import kotlinx.coroutines.flow.Flow

interface KeywordsRepository {

    val keywords: Flow<List<String>>
    val stopWords: Flow<List<String>>
    val telegramChannels: Flow<List<String>>
    val isTelegramChannelFilterEnabled: Flow<Boolean>

    suspend fun addKeyword(keyword: String)
    suspend fun removeKeyword(keyword: String)
    suspend fun addStopWord(stopWord: String)
    suspend fun removeStopWord(stopWord: String)
    suspend fun addTelegramChannel(channel: String)
    suspend fun removeTelegramChannel(channel: String)
    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean)
    suspend fun seedDefaultKeywordsIfNeeded()
    suspend fun restoreDefaultKeywords(): RestoreDefaultKeywordsResult
    suspend fun clearAllKeywordsPreferences()

}
