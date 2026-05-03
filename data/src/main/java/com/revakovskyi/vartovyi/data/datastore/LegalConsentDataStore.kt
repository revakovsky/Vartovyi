package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val DATASTORE_NAME = "vartovyi_legal_consent"

private val Context.legalConsentDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME
)

internal class LegalConsentDataStore(
    private val context: Context,
) {

    private object Keys {
        val ACCEPTED_LEGAL_DOCUMENTS_VERSION =
            intPreferencesKey("accepted_legal_documents_version")
    }

    val acceptedLegalDocumentsVersion: Flow<Int> = context.legalConsentDataStore.data
        .safeCatch()
        .map { preferences -> preferences[Keys.ACCEPTED_LEGAL_DOCUMENTS_VERSION] ?: 0 }

    suspend fun setAcceptedLegalDocumentsVersion(version: Int) {
        context.legalConsentDataStore.edit { preferences ->
            preferences[Keys.ACCEPTED_LEGAL_DOCUMENTS_VERSION] = version
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
