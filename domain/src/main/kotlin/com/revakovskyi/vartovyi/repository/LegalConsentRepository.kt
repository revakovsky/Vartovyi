package com.revakovskyi.vartovyi.repository

import kotlinx.coroutines.flow.Flow

interface LegalConsentRepository {

    val acceptedLegalDocumentsVersion: Flow<Int>

    suspend fun setAcceptedLegalDocumentsVersion(version: Int)

}

