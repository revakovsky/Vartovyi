package com.revakovskyi.vartovyi.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "vartovyi_legal_consent"

private val Context.legalConsentDataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATASTORE_NAME,
    corruptionHandler = preferencesCorruptionHandler(),
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

    suspend fun setAcceptedLegalDocumentsVersion(version: Int): Boolean {
        return context.legalConsentDataStore.safeEdit { preferences ->
            preferences[Keys.ACCEPTED_LEGAL_DOCUMENTS_VERSION] = version
        }
    }

}
