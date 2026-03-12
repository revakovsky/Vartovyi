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

class KeywordsDataStore(private val context: Context) {

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

    suspend fun setKeywords(keywords: List<String>) {
        context.keywordsDataStore.edit { it[Keys.KEYWORDS] = Json.encodeToString(keywords) }
    }

    suspend fun setStopWords(stopWords: List<String>) {
        context.keywordsDataStore.edit { it[Keys.STOP_WORDS] = Json.encodeToString(stopWords) }
    }

    suspend fun setTelegramChannels(channels: List<String>) {
        context.keywordsDataStore.edit {
            it[Keys.TELEGRAM_CHANNELS] = Json.encodeToString(channels)
        }
    }

    suspend fun setTelegramChannelFilterEnabled(enabled: Boolean) {
        context.keywordsDataStore.edit { it[Keys.TELEGRAM_CHANNEL_FILTER_ENABLED] = enabled }
    }

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }

}
