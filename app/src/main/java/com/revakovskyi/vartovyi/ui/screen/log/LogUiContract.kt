package com.revakovskyi.vartovyi.ui.screen.log

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.domain.model.AlertEvent

interface LogUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = false,
        val logEntries: List<AlertEvent> = emptyList(),
        val isClearDialogVisible: Boolean = false,
    )

    sealed interface Action {
        data object OpenClearLogDialog : Action
        data object DismissClearLogDialog : Action
        data object ConfirmClearLog : Action
        data class CopyChannelName(val channelName: String) : Action
        data class CopyMessageText(val messageText: String) : Action
    }

    sealed interface Event {
        data class CopyChannelNameRequested(val channelName: String) : Event
        data class CopyMessageTextRequested(val messageText: String) : Event
    }

}
