package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "vartovyi_onboarding"

private val Context.onboardingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
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

    suspend fun setOnboardingCompleted() {
        context.onboardingDataStore.edit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] = true
        }
    }

    private fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
        catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }

}
