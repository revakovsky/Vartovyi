package com.revakovskyi.vartovyi.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveLogSizeLimitUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveScheduleSettingsUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.ObserveTelegramPackagesUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetAlarmDurationUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetEndTimeUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetLogSizeLimitUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetScheduleEnabledUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetStartTimeUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetTelegramPackagesUseCase
import com.revakovskyi.vartovyi.domain.usecase.settings.SetVibrationEnabledUseCase
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

class SettingsViewModel(
    private val observeScheduleSettingsUseCase: ObserveScheduleSettingsUseCase,
    private val observeTelegramPackagesUseCase: ObserveTelegramPackagesUseCase,
    private val observeLogSizeLimitUseCase: ObserveLogSizeLimitUseCase,
    private val setScheduleEnabledUseCase: SetScheduleEnabledUseCase,
    private val setStartTimeUseCase: SetStartTimeUseCase,
    private val setEndTimeUseCase: SetEndTimeUseCase,
    private val setAlarmDurationUseCase: SetAlarmDurationUseCase,
    private val setVibrationEnabledUseCase: SetVibrationEnabledUseCase,
    private val setTelegramPackagesUseCase: SetTelegramPackagesUseCase,
    private val setLogSizeLimitUseCase: SetLogSizeLimitUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiContract.State())
    val state: StateFlow<SettingsUiContract.State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<SettingsUiContract.Event>()
    val events: SharedFlow<SettingsUiContract.Event> = _events.asSharedFlow()

    init {
        observeScheduleSettings()
        observeTelegramPackages()
        observeLogSizeLimit()
    }

    fun onAction(action: SettingsUiContract.Action) {
        when (action) {
            is SettingsUiContract.Action.SetScheduleEnabled -> setScheduleEnabled(action.enabled)
            is SettingsUiContract.Action.SetStartTime -> setStartTime(action.time)
            is SettingsUiContract.Action.SetEndTime -> setEndTime(action.time)
            is SettingsUiContract.Action.SetAlarmDuration -> setAlarmDuration(action.seconds)
            is SettingsUiContract.Action.SetVibrationEnabled -> setVibrationEnabled(action.enabled)
            is SettingsUiContract.Action.SetTelegramPackages -> setTelegramPackages(action.packages)
            is SettingsUiContract.Action.SetLogSizeLimit -> setLogSizeLimit(action.limit)
            is SettingsUiContract.Action.NavigateBack -> navigateBack()
        }
    }

    private fun observeScheduleSettings() {
        observeScheduleSettingsUseCase().onEach { scheduleSettings ->
            _state.update {
                it.copy(
                    isScheduleEnabled = scheduleSettings.isScheduleEnabled,
                    startTime = scheduleSettings.startTime,
                    endTime = scheduleSettings.endTime,
                    alarmDurationSeconds = scheduleSettings.alarmDurationSeconds,
                    isVibrationEnabled = scheduleSettings.isVibrationEnabled,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeTelegramPackages() {
        observeTelegramPackagesUseCase().onEach { packages ->
            _state.update { it.copy(selectedTelegramPackages = packages) }
        }.launchIn(viewModelScope)
    }

    private fun observeLogSizeLimit() {
        observeLogSizeLimitUseCase().onEach { limit ->
            _state.update { it.copy(logSizeLimit = limit) }
        }.launchIn(viewModelScope)
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

    private fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { setVibrationEnabledUseCase(enabled) }
    }

    private fun setTelegramPackages(packages: Set<String>) {
        viewModelScope.launch { setTelegramPackagesUseCase(packages) }
    }

    private fun setLogSizeLimit(limit: Int) {
        viewModelScope.launch { setLogSizeLimitUseCase(limit) }
    }

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(SettingsUiContract.Event.NavigateBack) }
    }

}
