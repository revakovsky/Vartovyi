package com.revakovskyi.vartovyi.ui.screen.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.usecase.log.ClearLogUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCase
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

    private val _state = MutableStateFlow(LogUiContract.State())
    val state: StateFlow<LogUiContract.State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<LogUiContract.Event>()
    val events: SharedFlow<LogUiContract.Event> = _events.asSharedFlow()

    init {
        observeLogEntries()
    }

    fun onAction(action: LogUiContract.Action) {
        when (action) {
            is LogUiContract.Action.ClearLog -> clearLog()
            is LogUiContract.Action.NavigateBack -> navigateBack()
        }
    }

    private fun observeLogEntries() {
        observeLogEntriesUseCase().onEach { entries ->
            _state.update { it.copy(logEntries = entries) }
        }.launchIn(viewModelScope)
    }

    private fun clearLog() {
        viewModelScope.launch {
            clearLogUseCase()
            _events.emit(LogUiContract.Event.LogCleared)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(LogUiContract.Event.NavigateBack) }
    }

}
