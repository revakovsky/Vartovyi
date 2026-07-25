package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

interface ObserveKeywordsChannelsIntroHiddenUseCase {
    operator fun invoke(): Flow<Boolean>
}

internal class ObserveKeywordsChannelsIntroHiddenUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : ObserveKeywordsChannelsIntroHiddenUseCase {

    override operator fun invoke(): Flow<Boolean> {
        return onboardingRepository.isKeywordsChannelsIntroHidden
    }

}
