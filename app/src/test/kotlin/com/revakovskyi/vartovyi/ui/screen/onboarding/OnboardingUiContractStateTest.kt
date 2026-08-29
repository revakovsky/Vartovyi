package com.revakovskyi.vartovyi.ui.screen.onboarding

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.revakovskyi.vartovyi.model.OnboardingPage
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingUiContract.PrimaryAction
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingUiContract.State
import org.junit.jupiter.api.Test

class OnboardingUiContractStateTest {

    @Test
    fun `isCityProvided is false for a single character, matching the domain minimum length`() {
        val state = State(pages = OnboardingPage.entries, currentPage = 1, cityInput = "Х")

        assertThat(state.isCityProvided).isFalse()
        assertThat(state.primaryAction).isEqualTo(PrimaryAction.SKIP_CITY)
    }

    @Test
    fun `isCityProvided is true once the city reaches the domain minimum length`() {
        val state = State(pages = OnboardingPage.entries, currentPage = 1, cityInput = "Хм")

        assertThat(state.isCityProvided).isTrue()
        assertThat(state.primaryAction).isEqualTo(PrimaryAction.SUBMIT_CITY)
    }

    @Test
    fun `isCityProvided ignores surrounding whitespace`() {
        val state = State(pages = OnboardingPage.entries, currentPage = 1, cityInput = "  Х  ")

        assertThat(state.isCityProvided).isFalse()
    }

}
