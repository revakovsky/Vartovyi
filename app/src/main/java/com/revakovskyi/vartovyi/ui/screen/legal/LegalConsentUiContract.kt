package com.revakovskyi.vartovyi.ui.screen.legal

object LegalConsentUiContract {

    data class State(
        val isLoading: Boolean = true,
        val isAccepted: Boolean = false,
    )

    sealed interface Action {
        data object OpenPrivacyPolicy : Action
        data object OpenTermsOfUse : Action
        data object Confirm : Action
        data object Refuse : Action
    }

    sealed interface Event {
        data class OpenUrl(val url: String) : Event
        data object CloseApplication : Event
    }

}
