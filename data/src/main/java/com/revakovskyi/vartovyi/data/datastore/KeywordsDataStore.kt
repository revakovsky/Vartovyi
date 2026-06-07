package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val DATASTORE_NAME = "vartovyi_keywords"

private val Context.keywordsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    corruptionHandler = preferencesCorruptionHandler(),
)

internal class KeywordsDataStore(private val context: Context) {

    private object Keys {
        val KEYWORDS = stringPreferencesKey("keywords")
        val STOP_WORDS = stringPreferencesKey("stop_words")
        val TELEGRAM_CHANNELS = stringPreferencesKey("telegram_channels")
        val TELEGRAM_CHANNEL_FILTER_ENABLED = booleanPreferencesKey("telegram_channel_filter_enabled")
    }

    val keywords: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> storedStringList(preferences, Keys.KEYWORDS) }

    val stopWords: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> storedStringList(preferences, Keys.STOP_WORDS) }

    val telegramChannels: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> storedStringList(preferences, Keys.TELEGRAM_CHANNELS) }

    val isTelegramChannelFilterEnabled: Flow<Boolean> = context.keywordsDataStore.data
        .safeCatch()
        .map { prefs -> prefs[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] ?: false }

    suspend fun addKeywordIfMissing(keyword: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentKeywords = storedStringList(preferences, Keys.KEYWORDS)
            if (keyword in currentKeywords) return@safeEdit

            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords + keyword)
        }
    }

    suspend fun removeKeyword(keyword: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentKeywords = storedStringList(preferences, Keys.KEYWORDS)
            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords - keyword)
        }
    }

    suspend fun addStopWordIfMissing(stopWord: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentStopWords = storedStringList(preferences, Keys.STOP_WORDS)
            if (stopWord in currentStopWords) return@safeEdit

            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords + stopWord)
        }
    }

    suspend fun removeStopWord(stopWord: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentStopWords = storedStringList(preferences, Keys.STOP_WORDS)
            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords - stopWord)
        }
    }

    suspend fun addTelegramChannelIfMissing(channel: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentChannels = storedStringList(preferences, Keys.TELEGRAM_CHANNELS)
            if (channel in currentChannels) return@safeEdit

            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels + channel)
        }
    }

    suspend fun removeTelegramChannel(channel: String): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val currentChannels = storedStringList(preferences, Keys.TELEGRAM_CHANNELS)
            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels - channel)
        }
    }

    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean): Boolean {
        return context.keywordsDataStore.safeEdit { it[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] = enabled }
    }

    suspend fun clearAllPreferences(): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            preferences.clear()
        }
    }

    private fun storedStringList(
        preferences: Preferences,
        key: Preferences.Key<String>,
    ): List<String> {
        return preferences[key]
            ?.let { storedValue -> Json.decodeFromString<List<String>>(storedValue) }
            ?: emptyList()
    }

}
