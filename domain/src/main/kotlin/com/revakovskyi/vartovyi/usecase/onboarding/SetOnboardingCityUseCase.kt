package com.revakovskyi.vartovyi.usecase.onboarding

import com.revakovskyi.vartovyi.repository.OnboardingRepository
import com.revakovskyi.vartovyi.usecase.keywords.normalizeCityForSeed

interface SetOnboardingCityUseCase {
    suspend operator fun invoke(city: String)
}

internal class SetOnboardingCityUseCaseImpl(
    private val onboardingRepository: OnboardingRepository,
) : SetOnboardingCityUseCase {

    /** Persists [city] in its normalized form; a blank or too-short city is not saved. */
    override suspend operator fun invoke(city: String) {
        val normalizedCity = normalizeCityForSeed(city) ?: return
        onboardingRepository.setOnboardingCity(normalizedCity)
    }

}
