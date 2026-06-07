package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.constants.DEFAULT_STOP_WORDS_SEED
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
    override val isTelegramChannelFilterEnabled: Flow<Boolean> =
        keywordsDataStore.isTelegramChannelFilterEnabled

    private val keywordsMutationMutex = Mutex()

    override suspend fun addKeyword(keyword: String): Boolean {
        if (keyword.isBlank()) return true

        val normalizedKeyword = keyword.trim()
        return keywordsMutationMutex.withLock {
            keywordsDataStore.addKeywordIfMissing(normalizedKeyword)
        }
    }

    override suspend fun removeKeyword(keyword: String): Boolean {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.removeKeyword(keyword)
        }
    }

    override suspend fun addStopWord(stopWord: String): Boolean {
        if (stopWord.isBlank()) return true

        val normalizedStopWord = stopWord.trim()
        return keywordsMutationMutex.withLock {
            keywordsDataStore.addStopWordIfMissing(normalizedStopWord)
        }
    }

    override suspend fun removeStopWord(stopWord: String): Boolean {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.removeStopWord(stopWord)
        }
    }

    override suspend fun addTelegramChannel(channel: String): Boolean {
        if (channel.isBlank()) return true

        val normalizedChannel = channel.trim()
        return keywordsMutationMutex.withLock {
            keywordsDataStore.addTelegramChannelIfMissing(normalizedChannel)
        }
    }

    override suspend fun removeTelegramChannel(channel: String): Boolean {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.removeTelegramChannel(channel)
        }
    }

    override suspend fun setTelegramChannelFilterEnabled(enabled: Boolean): Boolean {
        return keywordsDataStore.setTelegramChannelFilterEnabled(enabled)
    }

    override suspend fun seedDefaultKeywordsIfNeeded() {
        keywordsMutationMutex.withLock {
            keywordsDataStore.seedDefaultKeywordsIfNeeded(DEFAULT_KEYWORDS_SEED)
        }
    }

    override suspend fun seedDefaultStopWordsIfNeeded() {
        keywordsMutationMutex.withLock {
            keywordsDataStore.seedDefaultStopWordsIfNeeded(DEFAULT_STOP_WORDS_SEED)
        }
    }

    override suspend fun restoreDefaultKeywords(): Int {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.mergeKeywords(DEFAULT_KEYWORDS_SEED)
        }
    }

    override suspend fun restoreDefaultStopWords(): Int {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.mergeStopWords(DEFAULT_STOP_WORDS_SEED)
        }
    }

    override suspend fun clearAllKeywordsPreferences(): Boolean {
        return keywordsMutationMutex.withLock {
            keywordsDataStore.clearAllPreferences()
        }
    }

}
