package com.revakovskyi.vartovyi.ui.screen.onboarding

internal enum class OnboardingPage {
    WELCOME, TELEGRAM, KEYWORDS, LAUNCH,
}

object OnboardingUiContract {

    data class State(
        val isLoading: Boolean = true,
        val isCompleted: Boolean = false,
        val currentPage: Int = 0,
        val totalPages: Int = OnboardingPage.entries.size,
        val canSkip: Boolean = false,
    )

    sealed interface Action {
        data object NextPage : Action
        data object PreviousPage : Action
        data class PageChanged(val pageIndex: Int) : Action
        data object Complete : Action
        data object Skip : Action
        data object OpenKeywords : Action
    }

    sealed interface Event {
        data object Close : Event
        data object OpenKeywords : Event
    }

}
