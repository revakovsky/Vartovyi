package com.revakovskyi.vartovyi.usecase.keywords

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.model.KeywordsDataSnapshot
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApplyCityToSeededKeywordsUseCaseTest {

    private lateinit var repository: FakeKeywordsRepository
    private lateinit var onboardingRepository: FakeOnboardingRepository
    private lateinit var useCase: ApplyCityToSeededKeywordsUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeKeywordsRepository()
        onboardingRepository = FakeOnboardingRepository()
        useCase = ApplyCityToSeededKeywordsUseCaseImpl(
            keywordsRepository = repository,
            onboardingRepository = onboardingRepository,
        )
    }

    @Test
    fun `substitutes the city into an untouched placeholder template`() = runTest {
        repository.snapshot.value = repository.snapshot.value.copy(keywords = DEFAULT_KEYWORDS_SEED)

        val result = useCase("Харків")

        assertThat(result).isTrue()

        val rules =
            repository.snapshot.value.keywords.map { parseTriggerKeywordRuleFromStorage(it) }
        assertThat(rules).hasSize(3)
        assertThat(rules[0].type).isEqualTo(TriggerKeywordRuleType.WORD)
        assertThat(rules[1].type).isEqualTo(TriggerKeywordRuleType.ALL_WORDS)
        assertThat(rules[1].terms).isEqualTo(listOf("ракета", "Харків"))
        assertThat(rules[2].type).isEqualTo(TriggerKeywordRuleType.PHRASE)
        assertThat(rules[2].terms).isEqualTo(listOf("ціль на Харків"))
    }

    @Test
    fun `substitutes the city into an empty trigger list, covering the seeding race`() = runTest {
        repository.snapshot.value = repository.snapshot.value.copy(keywords = emptyList())

        val result = useCase("Харків")

        assertThat(result).isTrue()
        assertThat(repository.snapshot.value.keywords).hasSize(3)
    }

    @Test
    fun `does not touch a trigger list the user has already edited`() = runTest {
        val editedKeywords = listOf("шахед", "мій власний тригер")
        repository.snapshot.value = repository.snapshot.value.copy(keywords = editedKeywords)

        val result = useCase("Харків")

        assertThat(result).isFalse()
        assertThat(repository.snapshot.value.keywords).isEqualTo(editedKeywords)
    }

    @Test
    fun `re-applying with a different city stays idempotent with no duplicates`() = runTest {
        repository.snapshot.value = repository.snapshot.value.copy(keywords = DEFAULT_KEYWORDS_SEED)

        useCase("Харків")
        // Mirrors OnboardingViewModel.submitCity(): SetOnboardingCityUseCase persists the city
        // right after this use case runs, before the user could return and resubmit
        onboardingRepository.setOnboardingCity("Харків")
        val result = useCase("Львів")

        assertThat(result).isTrue()
        val keywords = repository.snapshot.value.keywords
        assertThat(keywords).hasSize(3)
        assertThat(keywords.any { it.contains("Харків") }).isFalse()
        assertThat(keywords.any { it.contains("Львів") }).isTrue()
    }

    @Test
    fun `a city-seeded list is not replaced when no previous city was ever saved`() = runTest {
        repository.snapshot.value = repository.snapshot.value.copy(
            keywords = buildCitySeededKeywords("Харків"),
        )

        val result = useCase("Львів")

        assertThat(result).isFalse()
        assertThat(repository.snapshot.value.keywords).isEqualTo(buildCitySeededKeywords("Харків"))
    }

    @Test
    fun `blank city is rejected without touching the data`() = runTest {
        val original = repository.snapshot.value

        val result = useCase("   ")

        assertThat(result).isFalse()
        assertThat(repository.snapshot.value).isEqualTo(original)
    }

    @Test
    fun `single-character city is rejected as too short`() = runTest {
        val original = repository.snapshot.value

        val result = useCase("Х")

        assertThat(result).isFalse()
        assertThat(repository.snapshot.value).isEqualTo(original)
    }

    @Test
    fun `stop words and telegram channels are preserved across the substitution`() = runTest {
        repository.snapshot.value = KeywordsDataSnapshot(
            keywords = DEFAULT_KEYWORDS_SEED,
            stopWords = listOf("відбій"),
            telegramChannels = listOf("@air_alert_ua"),
        )

        useCase("Харків")

        assertThat(repository.snapshot.value.stopWords).isEqualTo(listOf("відбій"))
        assertThat(repository.snapshot.value.telegramChannels)
            .isEqualTo(listOf("@air_alert_ua"))
    }

}
