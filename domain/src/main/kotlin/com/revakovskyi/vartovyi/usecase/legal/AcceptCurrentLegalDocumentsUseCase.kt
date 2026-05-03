package com.revakovskyi.vartovyi.usecase.legal

import com.revakovskyi.vartovyi.constants.CURRENT_LEGAL_DOCUMENTS_VERSION
import com.revakovskyi.vartovyi.repository.LegalConsentRepository

interface AcceptCurrentLegalDocumentsUseCase {
    suspend operator fun invoke()
}

class AcceptCurrentLegalDocumentsUseCaseImpl(
    private val legalConsentRepository: LegalConsentRepository,
) : AcceptCurrentLegalDocumentsUseCase {

    override suspend operator fun invoke() {
        legalConsentRepository.setAcceptedLegalDocumentsVersion(
            version = CURRENT_LEGAL_DOCUMENTS_VERSION,
        )
    }

}
