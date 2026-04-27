package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DATASTORE_NAME = "vartovyi_keywords"

private val Context.keywordsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
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
        .map { prefs ->
            prefs[Keys.KEYWORDS]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?: emptyList()
        }

    val stopWords: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { prefs ->
            prefs[Keys.STOP_WORDS]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?: emptyList()
        }

    val telegramChannels: Flow<List<String>> = context.keywordsDataStore.data
        .safeCatch()
        .map { prefs ->
            prefs[Keys.TELEGRAM_CHANNELS]
                ?.let { Json.decodeFromString<List<String>>(it) }
                ?: emptyList()
        }

    val isTelegramChannelFilterEnabled: Flow<Boolean> = context.keywordsDataStore.data
        .safeCatch()
        .map { prefs -> prefs[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] ?: false }

    suspend fun addKeywordIfMissing(keyword: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentKeywords = preferences[Keys.KEYWORDS]
                ?.let { storedKeywords -> Json.decodeFromString<List<String>>(storedKeywords) }
                ?: emptyList()
            if (keyword in currentKeywords) return@edit

            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords + keyword)
        }
    }

    suspend fun removeKeyword(keyword: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentKeywords = preferences[Keys.KEYWORDS]
                ?.let { storedKeywords -> Json.decodeFromString<List<String>>(storedKeywords) }
                ?: emptyList()
            preferences[Keys.KEYWORDS] = Json.encodeToString(currentKeywords - keyword)
        }
    }

    suspend fun addStopWordIfMissing(stopWord: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentStopWords = preferences[Keys.STOP_WORDS]
                ?.let { storedStopWords -> Json.decodeFromString<List<String>>(storedStopWords) }
                ?: emptyList()
            if (stopWord in currentStopWords) return@edit

            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords + stopWord)
        }
    }

    suspend fun removeStopWord(stopWord: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentStopWords = preferences[Keys.STOP_WORDS]
                ?.let { storedStopWords -> Json.decodeFromString<List<String>>(storedStopWords) }
                ?: emptyList()
            preferences[Keys.STOP_WORDS] = Json.encodeToString(currentStopWords - stopWord)
        }
    }

    suspend fun addTelegramChannelIfMissing(channel: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentChannels = preferences[Keys.TELEGRAM_CHANNELS]
                ?.let { storedChannels -> Json.decodeFromString<List<String>>(storedChannels) }
                ?: emptyList()
            if (channel in currentChannels) return@edit

            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels + channel)
        }
    }

    suspend fun removeTelegramChannel(channel: String) {
        context.keywordsDataStore.edit { preferences ->
            val currentChannels = preferences[Keys.TELEGRAM_CHANNELS]
                ?.let { storedChannels -> Json.decodeFromString<List<String>>(storedChannels) }
                ?: emptyList()
            preferences[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(currentChannels - channel)
        }
    }

    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        context.keywordsDataStore.edit { it[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] = enabled }
    }

    suspend fun clearAllPreferences() {
        context.keywordsDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }

}
