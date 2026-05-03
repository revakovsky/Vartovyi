package com.revakovskyi.vartovyi.data.repository

import com.revakovskyi.vartovyi.data.datastore.LegalConsentDataStore
import com.revakovskyi.vartovyi.repository.LegalConsentRepository
import kotlinx.coroutines.flow.Flow

internal class LegalConsentRepositoryImpl(
    private val legalConsentDataStore: LegalConsentDataStore,
) : LegalConsentRepository {

    override val acceptedLegalDocumentsVersion: Flow<Int> =
        legalConsentDataStore.acceptedLegalDocumentsVersion

    override suspend fun setAcceptedLegalDocumentsVersion(version: Int) {
        legalConsentDataStore.setAcceptedLegalDocumentsVersion(version)
    }

}
