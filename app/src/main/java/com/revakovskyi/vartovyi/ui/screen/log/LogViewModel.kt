package com.revakovskyi.vartovyi.ui.screen.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.usecase.log.ClearLogUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCase
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.log.LogUiContract.State
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LogViewModel(
    private val observeLogEntriesUseCase: ObserveLogEntriesUseCase,
    private val clearLogUseCase: ClearLogUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    init {
        observeLogEntries()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OpenClearLogDialog -> openClearLogDialog()
            is Action.DismissClearLogDialog -> dismissClearLogDialog()
            is Action.ConfirmClearLog -> confirmClearLog()
            is Action.CopyChannelName -> copyChannelName(channelName = action.channelName)
            is Action.CopyMessageText -> copyMessageText(messageText = action.messageText)
        }
    }

    private fun observeLogEntries() {
        observeLogEntriesUseCase().onEach { entries ->
            _state.update { it.copy(logEntries = entries) }
        }.launchIn(viewModelScope)
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
