package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.constants.DEFAULT_STOP_WORDS_SEED
import com.revakovskyi.vartovyi.data.datastore.KeywordsDataStore
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow

internal class KeywordsRepositoryImpl(
    private val keywordsDataStore: KeywordsDataStore,
) : KeywordsRepository {

    override val keywords: Flow<List<String>> = keywordsDataStore.keywords
    override val stopWords: Flow<List<String>> = keywordsDataStore.stopWords
    override val telegramChannels: Flow<List<String>> = keywordsDataStore.telegramChannels
    override val isTelegramChannelFilterEnabled: Flow<Boolean> =
        keywordsDataStore.isTelegramChannelFilterEnabled

    override suspend fun addKeyword(keyword: String) {
        if (keyword.isBlank()) return
        keywordsDataStore.addKeywordIfMissing(keyword.trim())
    }

    override suspend fun removeKeyword(keyword: String) {
        keywordsDataStore.removeKeyword(keyword)
    }

    override suspend fun addStopWord(stopWord: String) {
        if (stopWord.isBlank()) return
        keywordsDataStore.addStopWordIfMissing(stopWord.trim())
    }

    override suspend fun removeStopWord(stopWord: String) {
        keywordsDataStore.removeStopWord(stopWord)
    }

    override suspend fun addTelegramChannel(channel: String) {
        if (channel.isBlank()) return
        keywordsDataStore.addTelegramChannelIfMissing(channel.trim())
    }

    override suspend fun removeTelegramChannel(channel: String) {
        keywordsDataStore.removeTelegramChannel(channel)
    }

    override suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        keywordsDataStore.setTelegramChannelFilterEnabled(enabled)
    }

    override suspend fun seedDefaultKeywordsIfNeeded() {
        keywordsDataStore.seedDefaultKeywordsIfNeeded(DEFAULT_KEYWORDS_SEED)
    }

    override suspend fun seedDefaultStopWordsIfNeeded() {
        keywordsDataStore.seedDefaultStopWordsIfNeeded(DEFAULT_STOP_WORDS_SEED)
    }

    override suspend fun restoreDefaultKeywords(): Int {
        return keywordsDataStore.mergeKeywords(DEFAULT_KEYWORDS_SEED)
    }

    override suspend fun restoreDefaultStopWords(): Int {
        return keywordsDataStore.mergeStopWords(DEFAULT_STOP_WORDS_SEED)
    }

    override suspend fun clearAllKeywordsPreferences() {
        keywordsDataStore.clearAllPreferences()
    }

}
