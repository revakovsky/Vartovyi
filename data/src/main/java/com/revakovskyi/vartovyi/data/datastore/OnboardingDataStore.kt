package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "vartovyi_onboarding"

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    corruptionHandler = preferencesCorruptionHandler(),
)

internal class OnboardingDataStore(
    private val context: Context,
) {

    private object Keys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val IS_KEYWORDS_CHANNELS_INTRO_HIDDEN =
            booleanPreferencesKey("is_keywords_channels_intro_hidden")
        val IS_TELEGRAM_CHANNEL_REMINDER_SHOWN =
            booleanPreferencesKey("is_telegram_channel_reminder_shown")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.onboardingDataStore.data
        .safeCatch()
        .map { preferences -> preferences[Keys.IS_ONBOARDING_COMPLETED] ?: false }

    val isKeywordsChannelsIntroHidden: Flow<Boolean> = context.onboardingDataStore.data
        .safeCatch()
        .map { preferences -> preferences[Keys.IS_KEYWORDS_CHANNELS_INTRO_HIDDEN] ?: false }

    suspend fun setOnboardingCompleted(): Boolean {
        return context.onboardingDataStore.safeEdit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun setKeywordsChannelsIntroHidden(): Boolean {
        return context.onboardingDataStore.safeEdit { preferences ->
            preferences[Keys.IS_KEYWORDS_CHANNELS_INTRO_HIDDEN] = true
        }
    }

    /**
     * Reports whether the Telegram channel subscribe-reminder should be shown now — `true` only
     * the first time ever, `false` on every later call. Marks it as shown as a side effect
     */
    suspend fun shouldShowTelegramChannelReminder(): Boolean {
        var shouldShow = false
        val writeSucceeded = context.onboardingDataStore.safeEdit { preferences ->
            val alreadyShown = preferences[Keys.IS_TELEGRAM_CHANNEL_REMINDER_SHOWN] == true
            if (alreadyShown) return@safeEdit

            shouldShow = true
            preferences[Keys.IS_TELEGRAM_CHANNEL_REMINDER_SHOWN] = true
        }
        return shouldShow && writeSucceeded
    }

}
