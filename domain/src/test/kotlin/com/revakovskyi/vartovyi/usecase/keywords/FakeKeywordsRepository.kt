package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeKeywordsRepository : KeywordsRepository {

    val snapshot = MutableStateFlow(
        KeywordsDataSnapshot(
            keywords = emptyList(),
            stopWords = emptyList(),
            telegramChannels = emptyList(),
            isTelegramChannelFilterEnabled = false,
        )
    )

    var shouldFailWrite = false

    var replaceAllCallCount = 0

    override val keywords: Flow<List<String>> = snapshot.map { it.keywords }

    override val stopWords: Flow<List<String>> = snapshot.map { it.stopWords }

    override val telegramChannels: Flow<List<String>> = snapshot.map { it.telegramChannels }

    override val isTelegramChannelFilterEnabled: Flow<Boolean> =
        snapshot.map { it.isTelegramChannelFilterEnabled }

    override suspend fun addKeyword(keyword: String): Boolean = true

    override suspend fun removeKeyword(keyword: String): Boolean = true

    override suspend fun addStopWord(stopWord: String): Boolean = true

    override suspend fun removeStopWord(stopWord: String): Boolean = true

    override suspend fun addTelegramChannel(channel: String): Boolean = true

    override suspend fun removeTelegramChannel(channel: String): Boolean = true

    override suspend fun setTelegramChannelFilterEnabled(enabled: Boolean): Boolean = true

    override suspend fun seedDefaultKeywordsIfNeeded() = Unit

    override suspend fun seedDefaultStopWordsIfNeeded() = Unit

    override suspend fun restoreDefaultKeywords(): Int = 0

    override suspend fun restoreDefaultStopWords(): Int = 0

    override suspend fun clearAllKeywordsPreferences(): Boolean = true

    override suspend fun replaceAllKeywordsData(
        transform: (currentData: KeywordsDataSnapshot) -> KeywordsDataSnapshot,
    ): Boolean {
        replaceAllCallCount++
        if (shouldFailWrite) return false

        snapshot.value = transform(snapshot.value)
        return true
    }

}
