package com.revakovskyi.vartovyi.ui.screen.onboarding

import com.revakovskyi.vartovyi.model.OnboardingPage

object OnboardingUiContract {

    data class State(
        val isLoading: Boolean = true,
        val isCompleted: Boolean = false,
        val currentPage: Int = 0,
        val totalPages: Int = OnboardingPage.entries.size,
    )

    sealed interface Action {
        data object NextPage : Action
        data object PreviousPage : Action
        data class PageChanged(val pageIndex: Int) : Action
        data object Complete : Action
        data object Skip : Action
    }

    sealed interface Event {
        data object Close : Event
        data object ShowSkipHint : Event
    }

}
