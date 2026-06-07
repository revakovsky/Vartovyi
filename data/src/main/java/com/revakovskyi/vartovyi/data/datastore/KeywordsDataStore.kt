package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.revakovskyi.vartovyi.contract.CrashReporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private const val DATASTORE_NAME = "vartovyi_keywords"

private val Context.keywordsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    corruptionHandler = preferencesCorruptionHandler(),
)

internal class KeywordsDataStore(
    private val context: Context,
    private val crashReporter: CrashReporter,
) {

    private object Keys {
        val KEYWORDS = stringPreferencesKey("keywords")
        val STOP_WORDS = stringPreferencesKey("stop_words")
        val TELEGRAM_CHANNELS = stringPreferencesKey("telegram_channels")
        val TELEGRAM_CHANNEL_FILTER_ENABLED = booleanPreferencesKey("telegram_channel_filter_enabled")
        val KEYWORDS_SEEDED = booleanPreferencesKey("keywords_seeded")
        val STOP_WORDS_SEEDED = booleanPreferencesKey("stop_words_seeded")
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

    suspend fun seedDefaultKeywordsIfNeeded(defaults: List<String>): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val alreadySeeded = preferences[Keys.KEYWORDS_SEEDED] == true
            if (alreadySeeded) return@safeEdit

            preferences[Keys.KEYWORDS] = Json.encodeToString(defaults)
            preferences[Keys.KEYWORDS_SEEDED] = true
        }
    }

    suspend fun seedDefaultStopWordsIfNeeded(defaults: List<String>): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            val alreadySeeded = preferences[Keys.STOP_WORDS_SEEDED] == true
            if (alreadySeeded) return@safeEdit

            val currentStopWords = storedStringList(preferences, Keys.STOP_WORDS)
            if (currentStopWords.isEmpty()) {
                preferences[Keys.STOP_WORDS] = Json.encodeToString(defaults)
            }

            preferences[Keys.STOP_WORDS_SEEDED] = true
        }
    }

    suspend fun mergeKeywords(defaults: List<String>): Int {
        var addedCount = 0
        context.keywordsDataStore.safeEdit { preferences ->
            val currentKeywords = storedStringList(preferences, Keys.KEYWORDS)
            val missingKeywords = defaults.filter { keyword -> keyword !in currentKeywords }
            if (missingKeywords.isEmpty()) return@safeEdit

            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords + missingKeywords)
            addedCount = missingKeywords.size
        }
        return addedCount
    }

    suspend fun mergeStopWords(defaults: List<String>): Int {
        var addedCount = 0
        context.keywordsDataStore.safeEdit { preferences ->
            val currentStopWords = storedStringList(preferences, Keys.STOP_WORDS)
            val missingStopWords = defaults.filter { stopWord -> stopWord !in currentStopWords }
            if (missingStopWords.isEmpty()) return@safeEdit

            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords + missingStopWords)
            addedCount = missingStopWords.size
        }
        return addedCount
    }

    /**
     * Removes only the user data keys; `*_SEEDED` flags stay so cleared defaults are not
     * re-seeded on the next launch.
     */
    suspend fun clearAllPreferences(): Boolean {
        return context.keywordsDataStore.safeEdit { preferences ->
            preferences.remove(Keys.KEYWORDS)
            preferences.remove(Keys.STOP_WORDS)
            preferences.remove(Keys.TELEGRAM_CHANNELS)
            preferences.remove(Keys.TELEGRAM_CHANNEL_FILTER_ENABLED)
        }
    }

    private fun storedStringList(
        preferences: Preferences,
        key: Preferences.Key<String>,
    ): List<String> {
        return preferences[key]
            ?.let { storedValue ->
                runCatching { Json.decodeFromString<List<String>>(storedValue) }
                    .onFailure { throwable -> crashReporter.report(throwable) }
                    .getOrDefault(emptyList())
            }
            ?: emptyList()
    }

}
