package com.revakovskyi.vartovyi.repository

import kotlinx.coroutines.flow.Flow

interface KeywordsRepository {

    val keywords: Flow<List<String>>
    val stopWords: Flow<List<String>>
    val telegramChannels: Flow<List<String>>
    val isTelegramChannelFilterEnabled: Flow<Boolean>

    suspend fun addKeyword(keyword: String): Boolean
    suspend fun removeKeyword(keyword: String): Boolean
    suspend fun addStopWord(stopWord: String): Boolean
    suspend fun removeStopWord(stopWord: String): Boolean
    suspend fun addTelegramChannel(channel: String): Boolean
    suspend fun removeTelegramChannel(channel: String): Boolean
    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean): Boolean
    suspend fun clearAllKeywordsPreferences(): Boolean

}
