package com.revakovskyi.vartovyi.usecase.legal

import com.revakovskyi.vartovyi.constants.CURRENT_LEGAL_DOCUMENTS_VERSION
import com.revakovskyi.vartovyi.repository.LegalConsentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ObserveLegalConsentStateUseCase {
    operator fun invoke(): Flow<Boolean>
}

class ObserveLegalConsentStateUseCaseImpl(
    private val legalConsentRepository: LegalConsentRepository,
) : ObserveLegalConsentStateUseCase {

    override operator fun invoke(): Flow<Boolean> {
        return legalConsentRepository.acceptedLegalDocumentsVersion
            .map { acceptedVersion -> acceptedVersion >= CURRENT_LEGAL_DOCUMENTS_VERSION }
    }

}
