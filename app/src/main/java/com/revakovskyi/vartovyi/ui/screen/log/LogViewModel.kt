package com.revakovskyi.vartovyi.ui.screen.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.usecase.log.ClearLogUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.GetLogEntryIndexUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCase
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogViewModel(
    observeLogEntriesUseCase: ObserveLogEntriesUseCase,
    private val getLogEntryIndexUseCase: GetLogEntryIndexUseCase,
    private val clearLogUseCase: ClearLogUseCase,
) : ViewModel() {

    val pagedLogEntries: Flow<PagingData<AlertEvent>> =
        observeLogEntriesUseCase()
            .cachedIn(viewModelScope)

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.OpenClearLogDialog -> openClearLogDialog()
            is Action.DismissClearLogDialog -> dismissClearLogDialog()
            is Action.ConfirmClearLog -> confirmClearLog()
            is Action.CopyChannelName -> copyChannelName(channelName = action.channelName)
            is Action.CopyMessageText -> copyMessageText(messageText = action.messageText)
        }
    }

    suspend fun getLogEntryIndexById(eventId: String): Int {
        return getLogEntryIndexUseCase(eventId = eventId)
    }

    private fun openClearLogDialog() {
        _state.update { state ->
            state.copy(isClearDialogVisible = true)
        }
    }

    private fun dismissClearLogDialog() {
        _state.update { state ->
            state.copy(isClearDialogVisible = false)
        }
    }

    private fun confirmClearLog() {
        viewModelScope.launch {
            clearLogUseCase()

            _state.update { state ->
                state.copy(isClearDialogVisible = false)
            }
        }
    }

    private fun copyChannelName(channelName: String) {
        viewModelScope.launch {
            _events.emit(
                Event.CopyChannelNameRequested(
                    channelName = channelName,
                ),
            )
        }
    }

    private fun copyMessageText(messageText: String) {
        viewModelScope.launch {
            _events.emit(
                Event.CopyMessageTextRequested(
                    messageText = messageText,
                ),
            )
        }
    }

}
