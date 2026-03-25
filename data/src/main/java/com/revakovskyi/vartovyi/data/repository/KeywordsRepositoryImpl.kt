package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.datastore.KeywordsDataStore
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class KeywordsRepositoryImpl(
    private val keywordsDataStore: KeywordsDataStore,
) : KeywordsRepository {

    override val keywords: Flow<List<String>> = keywordsDataStore.keywords
    override val stopWords: Flow<List<String>> = keywordsDataStore.stopWords
    override val telegramChannels: Flow<List<String>> = keywordsDataStore.telegramChannels
    override val isTelegramChannelFilterEnabled: Flow<Boolean> = keywordsDataStore.isTelegramChannelFilterEnabled

    override suspend fun addKeyword(keyword: String) {
        if (keyword.isBlank()) return

        val currentKeywords = keywordsDataStore.keywords.first()
        if (keyword.trim() in currentKeywords) return

        keywordsDataStore.setKeywords(currentKeywords + keyword.trim())
    }

    override suspend fun removeKeyword(keyword: String) {
        val currentKeywords = keywordsDataStore.keywords.first()
        keywordsDataStore.setKeywords(currentKeywords - keyword)
    }

    override suspend fun addStopWord(stopWord: String) {
        if (stopWord.isBlank()) return

        val currentStopWords = keywordsDataStore.stopWords.first()
        if (stopWord.trim() in currentStopWords) return

        keywordsDataStore.setStopWords(currentStopWords + stopWord.trim())
    }

    override suspend fun removeStopWord(stopWord: String) {
        val currentStopWords = keywordsDataStore.stopWords.first()
        keywordsDataStore.setStopWords(currentStopWords - stopWord)
    }

    override suspend fun addTelegramChannel(channel: String) {
        if (channel.isBlank()) return

        val currentChannels = keywordsDataStore.telegramChannels.first()
        if (channel.trim() in currentChannels) return

        keywordsDataStore.setTelegramChannels(currentChannels + channel.trim())
    }

    override suspend fun removeTelegramChannel(channel: String) {
        val currentChannels = keywordsDataStore.telegramChannels.first()
        keywordsDataStore.setTelegramChannels(currentChannels - channel)
    }

    override suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        keywordsDataStore.setTelegramChannelFilterEnabled(enabled)
    }

}
