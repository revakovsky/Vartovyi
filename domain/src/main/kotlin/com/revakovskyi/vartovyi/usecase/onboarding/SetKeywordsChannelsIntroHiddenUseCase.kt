package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository

interface SetKeywordsChannelsIntroHiddenUseCase {
    suspend operator fun invoke()
}

internal class SetKeywordsChannelsIntroHiddenUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : SetKeywordsChannelsIntroHiddenUseCase {

    override suspend operator fun invoke() {
        onboardingRepository.setKeywordsChannelsIntroHidden()
    }

}
