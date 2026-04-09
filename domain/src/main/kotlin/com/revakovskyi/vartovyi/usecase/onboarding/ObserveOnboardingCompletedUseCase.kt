package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

interface ObserveOnboardingCompletedUseCase {
    operator fun invoke(): Flow<Boolean>
}

class ObserveOnboardingCompletedUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : ObserveOnboardingCompletedUseCase {

    override operator fun invoke(): Flow<Boolean> {
        return onboardingRepository.isOnboardingCompleted
    }

}
