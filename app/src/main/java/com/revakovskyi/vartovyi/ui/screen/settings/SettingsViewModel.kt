package com.revakovskyi.vartovyi.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsUiContract.State
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.usecase.alarm.TriggerAlarmUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveAlarmRetriggerCooldownDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveLogSizeLimitUseCase
import com.revakovskyi.vartovyi.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmRetriggerCooldownDurationUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmSoundUriUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetAlarmVolumeUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetEndTimeUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetLogSizeLimitUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetScheduleEnabledUseCase
import com.revakovskyi.vartovyi.usecase.settings.SetStartTimeUseCase
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

private const val SETTINGS_INITIAL_LOADING_SOURCES_TOTAL = 5

class SettingsViewModel(
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase,
    private val observeLogSizeLimitUseCase: ObserveLogSizeLimitUseCase,
    private val observeAlarmRetriggerCooldownDurationUseCase: ObserveAlarmRetriggerCooldownDurationUseCase,
    private val setScheduleEnabledUseCase: SetScheduleEnabledUseCase,
    private val setStartTimeUseCase: SetStartTimeUseCase,
    private val setEndTimeUseCase: SetEndTimeUseCase,
    private val setAlarmDurationUseCase: SetAlarmDurationUseCase,
    private val setAlarmSoundUriUseCase: SetAlarmSoundUriUseCase,
    private val setAlarmVolumeUseCase: SetAlarmVolumeUseCase,
    private val setLogSizeLimitUseCase: SetLogSizeLimitUseCase,
    private val setAlarmRetriggerCooldownDurationUseCase: SetAlarmRetriggerCooldownDurationUseCase,
    private val triggerAlarmUseCase: TriggerAlarmUseCase,
    private val stopAlarmUseCase: StopAlarmUseCase,
    private val observeAlarmRunningUseCase: ObserveAlarmRunningUseCase,
    private val observeMonitoringStateUseCase: ObserveMonitoringStateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private var loadedSourcesCount = 0

    init {
        observeScheduleSettings()
        observeLogSizeLimit()
        observeAlarmRetriggerCooldownDuration()
        observeAlarmRunning()
        observeMonitoringState()
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SetScheduleEnabled -> setScheduleEnabled(action.enabled)
            is Action.SetStartTime -> setStartTime(action.time)
            is Action.SetEndTime -> setEndTime(action.time)
            is Action.SetAlarmDuration -> setAlarmDuration(action.seconds)
            is Action.SetAlarmSoundUri -> setAlarmSoundUri(action.uri)
            is Action.SetAlarmVolume -> setAlarmVolume(action.percent)
            is Action.SetLogSizeLimit -> setLogSizeLimit(action.limit)
            is Action.SetAlarmRetriggerCooldownDurationMillis ->
                setAlarmRetriggerCooldownDurationMillis(durationMillis = action.durationMillis)

            is Action.ToggleTestAlarm -> toggleTestAlarm(
                sourceChannelName = action.sourceChannelName,
                sourceMessageText = action.sourceMessageText,
            )
        }
    }

    private fun observeScheduleSettings() {
        var isFirstEmission = true
        observeScheduleSettingsUseCase().onEach { scheduleSettings ->
            _state.update {
                it.copy(
                    isScheduleEnabled = scheduleSettings.isScheduleEnabled,
                    startTime = scheduleSettings.startTime,
                    endTime = scheduleSettings.endTime,
                    alarmDurationSeconds = scheduleSettings.alarmDurationSeconds,
                    alarmVolumePercent = scheduleSettings.alarmVolumePercent,
                    alarmSoundUri = scheduleSettings.alarmSoundUri,
                )
            }

            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeLogSizeLimit() {
        var isFirstEmission = true
        observeLogSizeLimitUseCase().onEach { limit ->
            _state.update { it.copy(logSizeLimit = limit) }

            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeAlarmRetriggerCooldownDuration() {
        var isFirstEmission = true
        observeAlarmRetriggerCooldownDurationUseCase().onEach { durationMillis ->
            _state.update {
                it.copy(alarmRetriggerCooldownDurationMillis = durationMillis)
            }

            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeAlarmRunning() {
        var isFirstEmission = true
        observeAlarmRunningUseCase().onEach { isRunning ->
            _state.update { it.copy(isAlarmRunning = isRunning) }

            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun observeMonitoringState() {
        var isFirstEmission = true
        observeMonitoringStateUseCase().onEach { monitoringState ->
            _state.update {
                it.copy(isMonitoringActive = monitoringState == MonitoringState.ACTIVE)
            }

            if (isFirstEmission) {
                isFirstEmission = false
                markSourceLoaded()
            }
        }.launchIn(viewModelScope)
    }

    private fun markSourceLoaded() {
        loadedSourcesCount++

        if (loadedSourcesCount >= SETTINGS_INITIAL_LOADING_SOURCES_TOTAL) {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun setScheduleEnabled(enabled: Boolean) {
        viewModelScope.launch { setScheduleEnabledUseCase(enabled) }
    }

    private fun setStartTime(time: String) {
        viewModelScope.launch { setStartTimeUseCase(time) }
    }

    private fun setEndTime(time: String) {
        viewModelScope.launch { setEndTimeUseCase(time) }
    }

    private fun setAlarmDuration(seconds: Int) {
        viewModelScope.launch { setAlarmDurationUseCase(seconds) }
    }

    private fun setAlarmSoundUri(uri: String) {
        viewModelScope.launch { setAlarmSoundUriUseCase(uri) }
    }

    private fun setAlarmVolume(percent: Int) {
        viewModelScope.launch { setAlarmVolumeUseCase(percent) }
    }

    private fun setLogSizeLimit(limit: Int) {
        viewModelScope.launch { setLogSizeLimitUseCase(limit) }
    }

    private fun setAlarmRetriggerCooldownDurationMillis(durationMillis: Long) {
        viewModelScope.launch {
            setAlarmRetriggerCooldownDurationUseCase(durationMillis)
        }
    }

    private fun toggleTestAlarm(
        sourceChannelName: String,
        sourceMessageText: String,
    ) {
        if (state.value.isMonitoringActive) {
            viewModelScope.launch {
                _events.emit(Event.ShowDisableMonitoringForTestAlarm)
            }
            return
        }

        if (state.value.isAlarmRunning) {
            stopAlarmUseCase()
        } else {
            triggerAlarmUseCase(
                sourceChannelName = sourceChannelName,
                sourceMessageText = sourceMessageText,
            )
        }
    }

}
