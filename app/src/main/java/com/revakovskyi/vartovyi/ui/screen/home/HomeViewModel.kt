package com.revakovskyi.vartovyi.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract.State
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRetriggerCooldownUseCase
import com.revakovskyi.vartovyi.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.usecase.log.ObserveLastAlarmTriggeredEventUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveScheduleSettingsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val observeMonitoringStateUseCase: ObserveMonitoringStateUseCase,
    private val toggleMonitoringUseCase: ToggleMonitoringUseCase,
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase,
    private val observeKeywordsUseCase: ObserveKeywordsUseCase,
    private val observeLastAlarmTriggeredEventUseCase: ObserveLastAlarmTriggeredEventUseCase,
    private val observeAlarmRetriggerCooldownUseCase: ObserveAlarmRetriggerCooldownUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private var loadedSourcesCount = 0

    init {
        observeMonitoringState()
        observeScheduleSettings()
        observeKeywords()
        observeLastAlertEvent()
        observeAlarmRetriggerCooldown()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleMonitoring -> toggleMonitoring()
            is Action.NavigateToKeywords -> navigateToKeywords()
            is Action.NavigateToLog -> navigateToLog(logEntryId = action.logEntryId)
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
        observeLastAlarmTriggeredEventUseCase().onEach { lastAlarmTriggeredEvent ->
            _state.update {
                it.copy(
                    lastAlertEvent = lastAlarmTriggeredEvent
                )
            }
            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeAlarmRetriggerCooldown() {
        observeAlarmRetriggerCooldownUseCase().onEach { remainingCooldownMillis ->
            _state.update {
                it.copy(alarmRetriggerCooldownMillis = remainingCooldownMillis)
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
        viewModelScope.launch { _events.send(Event.NavigateToKeywords) }
    }

    private fun navigateToLog(logEntryId: String?) {
        viewModelScope.launch {
            _events.send(
                Event.NavigateToLog(
                    logEntryId = logEntryId,
                ),
            )
        }
    }

}
