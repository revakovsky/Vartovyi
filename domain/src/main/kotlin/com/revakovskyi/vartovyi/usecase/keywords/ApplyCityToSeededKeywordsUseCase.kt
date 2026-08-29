package com.revakovskyi.vartovyi.usecase.keywords

import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.repository.KeywordsRepository
import com.revakovskyi.vartovyi.repository.OnboardingRepository
import kotlinx.coroutines.flow.first

interface ApplyCityToSeededKeywordsUseCase {
    suspend operator fun invoke(city: String): Boolean
}

internal class ApplyCityToSeededKeywordsUseCaseImpl(
    private val keywordsRepository: KeywordsRepository,
    private val onboardingRepository: OnboardingRepository,
) : ApplyCityToSeededKeywordsUseCase {

    override suspend operator fun invoke(city: String): Boolean {
        val normalizedCity = normalizeCityForSeed(city) ?: return false

        val currentKeywords = keywordsRepository.keywords.first()
        val previousCity = onboardingRepository.onboardingCity.first()
        if (!isReplaceableWithCity(currentKeywords, previousCity)) return false

        val newKeywords = buildCitySeededKeywords(normalizedCity)
        return keywordsRepository.replaceAllKeywordsData { snapshot ->
            snapshot.copy(keywords = newKeywords)
        }
    }

    /**
     * True when [currentKeywords] is untouched (empty or the placeholder template), or is the
     * template with [previousCity] already substituted — the "returned and changed city" case
     */
    private fun isReplaceableWithCity(
        currentKeywords: List<String>,
        previousCity: String,
    ): Boolean {
        if (currentKeywords.isEmpty()) return true

        val currentSet = currentKeywords.toSet()
        if (currentSet == DEFAULT_KEYWORDS_SEED.toSet()) return true
        if (previousCity.isBlank()) return false

        return currentSet == buildCitySeededKeywords(previousCity).toSet()
    }

}
