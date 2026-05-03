package com.revakovskyi.vartovyi.ui.screen.log

import androidx.compose.runtime.Immutable

interface LogUiContract {

    enum class LogContentViewState {
        Loading, Error, Empty, Content,
    }

    @Immutable
    data class State(
        val contentViewState: LogContentViewState = LogContentViewState.Loading,
        val highlightLogEntryId: String? = null,
        val highlightedLogEntryIndex: Int = -1,
        val isClearDialogVisible: Boolean = false,
    )

    sealed interface Action {
        data class SyncLogListPresentation(
            val isRefreshLoading: Boolean,
            val isRefreshError: Boolean,
            val itemCount: Int,
        ) : Action

        data class SyncHighlightLogEntry(val logEntryId: String?) : Action

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
