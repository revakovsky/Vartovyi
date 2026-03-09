package com.revakovskyi.vartovyi.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.domain.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.domain.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.domain.usecase.alarm.TriggerAlarmUseCase
import com.revakovskyi.vartovyi.domain.usecase.keywords.ObserveKeywordsUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.domain.usecase.monitoring.ToggleMonitoringUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCase
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
    private val triggerAlarmUseCase: TriggerAlarmUseCase,
    private val stopAlarmUseCase: StopAlarmUseCase,
    private val observeAlarmRunningUseCase: ObserveAlarmRunningUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiContract.State())
    val state: StateFlow<HomeUiContract.State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<HomeUiContract.Event>()
    val events: SharedFlow<HomeUiContract.Event> = _events.asSharedFlow()

    init {
        observeMonitoringState()
        observeScheduleSettings()
        observeKeywords()
        observeAlarmRunning()
    }

    fun onAction(action: HomeUiContract.Action) {
        when (action) {
            is HomeUiContract.Action.ToggleMonitoring -> toggleMonitoring()
            is HomeUiContract.Action.TestAlarm -> testAlarm()
            is HomeUiContract.Action.NavigateToKeywords -> navigateToKeywords()
            is HomeUiContract.Action.NavigateToLog -> navigateToLog()
            is HomeUiContract.Action.NavigateToSettings -> navigateToSettings()
            is HomeUiContract.Action.NavigateToPermissions -> navigateToPermissions()
        }
    }

    private fun observeMonitoringState() {
        observeMonitoringStateUseCase().onEach { monitoringState ->
            _state.update { it.copy(monitoringState = monitoringState) }
        }.launchIn(viewModelScope)
    }

    private fun observeScheduleSettings() {
        observeScheduleSettingsUseCase().onEach { scheduleSettings ->
            _state.update {
                it.copy(
                    isScheduleEnabled = scheduleSettings.isScheduleEnabled,
                    startTime = scheduleSettings.startTime,
                    endTime = scheduleSettings.endTime,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeKeywords() {
        observeKeywordsUseCase().onEach { keywords ->
            _state.update { it.copy(keywords = keywords) }
        }.launchIn(viewModelScope)
    }

    private fun observeAlarmRunning() {
        observeAlarmRunningUseCase().onEach { isRunning ->
            _state.update { it.copy(isAlarmRunning = isRunning) }
        }.launchIn(viewModelScope)
    }

    private fun toggleMonitoring() {
        viewModelScope.launch {
            val isCurrentlyActive = state.value.monitoringState == MonitoringState.ACTIVE
            toggleMonitoringUseCase(isCurrentlyActive)
            val event =
                if (isCurrentlyActive) HomeUiContract.Event.MonitoringStopped
                else HomeUiContract.Event.MonitoringStarted
            _events.emit(event)
        }
    }

    private fun testAlarm() {
        if (state.value.isAlarmRunning) stopAlarmUseCase()
        else triggerAlarmUseCase()
    }

    private fun navigateToKeywords() {
        viewModelScope.launch { _events.emit(HomeUiContract.Event.NavigateToKeywords) }
    }

    private fun navigateToLog() {
        viewModelScope.launch { _events.emit(HomeUiContract.Event.NavigateToLog) }
    }

    private fun navigateToSettings() {
        viewModelScope.launch { _events.emit(HomeUiContract.Event.NavigateToSettings) }
    }

    private fun navigateToPermissions() {
        viewModelScope.launch { _events.emit(HomeUiContract.Event.NavigateToPermissions) }
    }

}
