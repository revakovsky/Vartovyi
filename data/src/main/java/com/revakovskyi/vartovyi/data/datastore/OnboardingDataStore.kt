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
    }

    val isOnboardingCompleted: Flow<Boolean> = context.onboardingDataStore.data
        .safeCatch()
        .map { preferences -> preferences[Keys.IS_ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(): Boolean {
        return context.onboardingDataStore.safeEdit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] = true
        }
    }

}
