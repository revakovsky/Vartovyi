package com.revakovskyi.vartovyi.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

private const val DATASTORE_SAFETY_TAG = "DataStoreSafety"

internal fun preferencesCorruptionHandler(): ReplaceFileCorruptionHandler<Preferences> =
    ReplaceFileCorruptionHandler { corruptionException ->
        Log.e(DATASTORE_SAFETY_TAG, "Preferences file corrupted, resetting", corruptionException)
        emptyPreferences()
    }

internal fun Flow<Preferences>.safeCatch(): Flow<Preferences> =
    catch { error ->
        if (error is IOException) {
            Log.e(DATASTORE_SAFETY_TAG, "Failed to read preferences", error)
            emit(emptyPreferences())
        } else {
            throw error
        }
    }

internal suspend fun DataStore<Preferences>.safeEdit(
    transform: suspend (preferences: MutablePreferences) -> Unit,
): Boolean {
    return try {
        edit(transform)
        true
    } catch (error: IOException) {
        Log.e(DATASTORE_SAFETY_TAG, "Failed to write preferences", error)
        false
    }
}
