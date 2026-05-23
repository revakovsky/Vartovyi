package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.revakovskyi.vartovyi.contract.CrashReporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DATASTORE_NAME = "vartovyi_keywords"

private val Context.keywordsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
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
    }

    val keywords: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> preferences.decodeStringList(Keys.KEYWORDS) }

    val stopWords: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> preferences.decodeStringList(Keys.STOP_WORDS) }

    val telegramChannels: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { preferences -> preferences.decodeStringList(Keys.TELEGRAM_CHANNELS) }

    val isTelegramChannelFilterEnabled: Flow<Boolean> = context.keywordsDataStore.data
        .safeCatch()
        .map { prefs -> prefs[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] ?: false }

    suspend fun addKeywordIfMissing(keyword: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentKeywords = preferences.decodeStringList(Keys.KEYWORDS)
            if (keyword in currentKeywords) return@edit
            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords + keyword)
        }
    }

    suspend fun removeKeyword(keyword: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentKeywords = preferences.decodeStringList(Keys.KEYWORDS)
            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords - keyword)
        }
    }

    suspend fun addStopWordIfMissing(stopWord: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentStopWords = preferences.decodeStringList(Keys.STOP_WORDS)
            if (stopWord in currentStopWords) return@edit
            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords + stopWord)
        }
    }

    suspend fun removeStopWord(stopWord: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentStopWords = preferences.decodeStringList(Keys.STOP_WORDS)
            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords - stopWord)
        }
    }

    suspend fun addTelegramChannelIfMissing(channel: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentChannels = preferences.decodeStringList(Keys.TELEGRAM_CHANNELS)
            if (channel in currentChannels) return@edit
            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels + channel)
        }
    }

    suspend fun removeTelegramChannel(channel: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentChannels = preferences.decodeStringList(Keys.TELEGRAM_CHANNELS)
            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels - channel)
        }
    }

    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        context.keywordsDataStore.edit { it[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] = enabled }
    }

    suspend fun seedDefaultKeywordsIfNeeded(defaults: List<String>) {
        context.keywordsDataStore.edit { preferences ->
            val alreadySeeded = preferences[Keys.KEYWORDS_SEEDED] == true
            if (alreadySeeded) return@edit

            preferences[Keys.KEYWORDS] = Json.encodeToString(defaults)
            preferences[Keys.KEYWORDS_SEEDED] = true
        }
    }

    suspend fun mergeKeywords(defaults: List<String>): Int {
        var addedCount = 0
        context.keywordsDataStore.edit { preferences ->
            val currentKeywords = preferences.decodeStringList(Keys.KEYWORDS)
            val missingKeywords = defaults.filter { keyword -> keyword !in currentKeywords }
            if (missingKeywords.isEmpty()) return@edit

            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords + missingKeywords)
            addedCount = missingKeywords.size
        }
        return addedCount
    }

    suspend fun clearAllPreferences() {
        context.keywordsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun Preferences.decodeStringList(key: Preferences.Key<String>): List<String> =
        this[key]
            ?.let { stored ->
                runCatching { Json.decodeFromString<List<String>>(stored) }
                    .onFailure { throwable -> crashReporter.report(throwable) }
                    .getOrDefault(emptyList())
            }
            ?: emptyList()

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }

}
