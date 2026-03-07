package com.revakovskyi.vartovyi.ui.screen.log

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.domain.model.AlertEvent

interface LogUiContract {

    @Immutable
    data class State(
        val logEntries: List<AlertEvent> = emptyList(),
        val isLoading: Boolean = false,
    )

    sealed interface Action {
        data object ClearLog : Action
        data object NavigateBack : Action
    }

    sealed interface Event {
        data object LogCleared : Event
        data object NavigateBack : Event
        data class Error(val message: String) : Event
    }

}
