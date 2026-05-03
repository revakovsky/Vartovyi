package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository

interface SetOnboardingCompletedUseCase {
    suspend operator fun invoke()
}

class SetOnboardingCompletedUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : SetOnboardingCompletedUseCase {

    override suspend operator fun invoke() {
        onboardingRepository.setOnboardingCompleted()
    }

}
