package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.datastore.KeywordsDataStore
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class KeywordsRepositoryImpl(
    private val keywordsDataStore: KeywordsDataStore,
) : KeywordsRepository {

    override val keywords: Flow<List<String>> = keywordsDataStore.keywords
    override val stopWords: Flow<List<String>> = keywordsDataStore.stopWords
    override val telegramChannels: Flow<List<String>> = keywordsDataStore.telegramChannels
    override val isTelegramChannelFilterEnabled: Flow<Boolean> = keywordsDataStore.isTelegramChannelFilterEnabled
    private val keywordsMutationMutex = Mutex()

    override suspend fun addKeyword(keyword: String) {
        if (keyword.isBlank()) return

        val normalizedKeyword = keyword.trim()
        keywordsMutationMutex.withLock {
            keywordsDataStore.addKeywordIfMissing(normalizedKeyword)
        }
    }

    override suspend fun removeKeyword(keyword: String) {
        keywordsMutationMutex.withLock {
            keywordsDataStore.removeKeyword(keyword)
        }
    }

    override suspend fun addStopWord(stopWord: String) {
        if (stopWord.isBlank()) return

        val normalizedStopWord = stopWord.trim()
        keywordsMutationMutex.withLock {
            keywordsDataStore.addStopWordIfMissing(normalizedStopWord)
        }
    }

    override suspend fun removeStopWord(stopWord: String) {
        keywordsMutationMutex.withLock {
            keywordsDataStore.removeStopWord(stopWord)
        }
    }

    override suspend fun addTelegramChannel(channel: String) {
        if (channel.isBlank()) return

        val normalizedChannel = channel.trim()
        keywordsMutationMutex.withLock {
            keywordsDataStore.addTelegramChannelIfMissing(normalizedChannel)
        }
    }

    override suspend fun removeTelegramChannel(channel: String) {
        keywordsMutationMutex.withLock {
            keywordsDataStore.removeTelegramChannel(channel)
        }
    }

    override suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        keywordsDataStore.setTelegramChannelFilterEnabled(enabled)
    }

    override suspend fun clearAllKeywordsPreferences() {
        keywordsDataStore.clearAllPreferences()
    }

}
