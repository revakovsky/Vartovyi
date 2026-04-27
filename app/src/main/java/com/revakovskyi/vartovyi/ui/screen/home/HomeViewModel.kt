package com.revakovskyi.vartovyi.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.model.AlertEvent
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val HOME_VIEW_MODEL_TAG = "HomeViewModel"

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

    init {
        observeHomeState()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.ToggleMonitoring -> toggleMonitoring()
            is Action.NavigateToKeywords -> navigateToKeywords()
            is Action.NavigateToLog -> navigateToLog(logEntryId = action.logEntryId)
        }
    }

    private fun observeHomeState() {
        combine(
            observeMonitoringStateUseCase(),
            observeScheduleSettingsUseCase(),
            observeKeywordsUseCase(),
            observeLastAlarmTriggeredEventUseCase(),
            observeAlarmRetriggerCooldownUseCase(),
        ) {
                monitoringState,
                scheduleSettings,
                keywords,
                lastAlarmTriggeredEvent,
                remainingCooldownMillis,
            ->
            HomeCombinedState(
                monitoringState = monitoringState,
                isScheduleEnabled = scheduleSettings.isScheduleEnabled,
                startTime = scheduleSettings.startTime,
                endTime = scheduleSettings.endTime,
                keywords = keywords,
                lastAlertEvent = lastAlarmTriggeredEvent,
                alarmRetriggerCooldownMillis = remainingCooldownMillis,
            )
        }.onEach { combinedState ->
            _state.update {
                it.copy(
                    monitoringState = combinedState.monitoringState,
                    isScheduleEnabled = combinedState.isScheduleEnabled,
                    startTime = combinedState.startTime,
                    endTime = combinedState.endTime,
                    keywords = combinedState.keywords,
                    lastAlertEvent = combinedState.lastAlertEvent,
                    alarmRetriggerCooldownMillis = combinedState.alarmRetriggerCooldownMillis,
                    isLoading = false,
                )
            }
        }.catch { throwable ->
            Log.e(HOME_VIEW_MODEL_TAG, "Failed to combine home state", throwable)
            _state.update { it.copy(isLoading = false) }
        }.launchIn(viewModelScope)
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

    private data class HomeCombinedState(
        val monitoringState: MonitoringState,
        val isScheduleEnabled: Boolean,
        val startTime: String,
        val endTime: String,
        val keywords: List<String>,
        val lastAlertEvent: AlertEvent?,
        val alarmRetriggerCooldownMillis: Long,
    )

}
