package com.revakovskyi.vartovyi.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.log.ObserveLogEntriesUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.State
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

class HomeViewModel(
    private val observeMonitoringStateUseCase: ObserveMonitoringStateUseCase,
    private val toggleMonitoringUseCase: ToggleMonitoringUseCase,
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase,
    private val observeKeywordsUseCase: ObserveKeywordsUseCase,
    private val observeLogEntriesUseCase: ObserveLogEntriesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var loadedSourcesCount = 0

    init {
        observeMonitoringState()
        observeScheduleSettings()
        observeKeywords()
        observeLastAlertEvent()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleMonitoring -> toggleMonitoring()
            is Action.NavigateToKeywords -> navigateToKeywords()
            is Action.NavigateToLog -> navigateToLog()
        }
    }

    private fun observeMonitoringState() {
        var isFirstEmission = true
        observeMonitoringStateUseCase().onEach { monitoringState ->
            _state.update { it.copy(monitoringState = monitoringState) }
            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeScheduleSettings() {
        var isFirstEmission = true
        observeScheduleSettingsUseCase().onEach { scheduleSettings ->
            _state.update {
                it.copy(
                    isScheduleEnabled = scheduleSettings.isScheduleEnabled,
                    startTime = scheduleSettings.startTime,
                    endTime = scheduleSettings.endTime,
                )
            }
            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeKeywords() {
        var isFirstEmission = true
        observeKeywordsUseCase().onEach { keywords ->
            _state.update { it.copy(keywords = keywords) }
            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeLastAlertEvent() {
        var isFirstEmission = true
        observeLogEntriesUseCase().onEach { logEntries ->
            _state.update {
                it.copy(
                    lastAlertEvent = logEntries.firstOrNull { event ->
                        event.matchedKeyword.isNotBlank()
                    }
                )
            }
            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun markSourceLoaded() {
        loadedSourcesCount++
        if (loadedSourcesCount >= 4) {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun toggleMonitoring() {
        viewModelScope.launch {
            val isCurrentlyActive = state.value.monitoringState == MonitoringState.ACTIVE
            toggleMonitoringUseCase(isCurrentlyActive)
        }
    }

    private fun navigateToKeywords() {
        viewModelScope.launch { _events.emit(Event.NavigateToKeywords) }
    }

    private fun navigateToLog() {
        viewModelScope.launch { _events.emit(Event.NavigateToLog) }
    }

}
