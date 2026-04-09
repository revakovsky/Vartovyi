package com.revakovskyi.vartovyi.ui.screen.onboarding

private const val TOTAL_PAGES = 5

object OnboardingUiContract {

    data class State(
        val currentPage: Int = 0,
        val totalPages: Int = TOTAL_PAGES,
        val canSkip: Boolean = false,
    )

    sealed interface Action {
        data object NextPage : Action
        data object PreviousPage : Action
        data class PageChanged(val pageIndex: Int) : Action
        data object Complete : Action
        data object Skip : Action
        data object ShowManually : Action
    }

    sealed interface Event {
        data object Close : Event
    }

}
