package com.revakovskyi.vartovyi.ui.screen.onboarding

import com.revakovskyi.vartovyi.constants.KeywordsLimits
import com.revakovskyi.vartovyi.model.OnboardingPage

object OnboardingUiContract {

    /** What tapping the primary bottom button should do, decided by [State] rather than the UI. */
    enum class PrimaryAction { NEXT, COMPLETE, SKIP_CITY, SUBMIT_CITY }

    data class State(
        val isLoading: Boolean = true,
        val isCompleted: Boolean = false,
        val currentPage: Int = 0,
        val pages: List<OnboardingPage> = OnboardingPage.entries,
        val cityInput: String = "",
    ) {
        val totalPages: Int get() = pages.size

        /**
         * Matches the minimum length the domain layer requires to accept a city, so the button
         * never promises a submit that silently does nothing
         */
        val isCityProvided: Boolean
            get() = cityInput.trim().length >= KeywordsLimits.MIN_TERM_LENGTH

        private val isLastPage: Boolean get() = currentPage == totalPages - 1

        private val isCityPage: Boolean get() = pages.getOrNull(currentPage) == OnboardingPage.CITY

        /** [isCityPage] is checked before [isLastPage] — CITY must never be the last page */
        val primaryAction: PrimaryAction
            get() = when {
                isCityPage && isCityProvided -> PrimaryAction.SUBMIT_CITY
                isCityPage -> PrimaryAction.SKIP_CITY
                isLastPage -> PrimaryAction.COMPLETE
                else -> PrimaryAction.NEXT
            }
    }

    sealed interface Action {
        data object NextPage : Action
        data object PreviousPage : Action
        data class PageChanged(val pageIndex: Int) : Action
        data object Complete : Action
        data object Skip : Action
        data class UpdateCityInput(val value: String) : Action
        data object SubmitCity : Action
        data object SkipCity : Action
    }

    sealed interface Event {
        data object Close : Event
        data object ShowSkipHint : Event
    }

}
