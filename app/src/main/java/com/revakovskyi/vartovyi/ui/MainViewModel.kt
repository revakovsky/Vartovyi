package com.revakovskyi.vartovyi.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.SyncMonitoringRuntimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MAIN_VIEW_MODEL_TAG = "MainViewModel"

class MainViewModel(
    private val observeAlarmRunningUseCase: ObserveAlarmRunningUseCase,
    private val observeMonitoringStateUseCase: ObserveMonitoringStateUseCase,
    private val syncMonitoringRuntimeUseCase: SyncMonitoringRuntimeUseCase,
    private val stopAlarmUseCase: StopAlarmUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(MainUiContract.State())
    val state: StateFlow<MainUiContract.State> = _state.asStateFlow()

    init {
        observeAlarmRunning()
        observeMonitoringState()
    }

    fun onAction(action: MainUiContract.Action) {
        when (action) {
            is MainUiContract.Action.StopAlarm -> stopAlarm()
            is MainUiContract.Action.SyncMonitoringRuntime -> syncMonitoringRuntime()
        }
    }

    private fun observeAlarmRunning() {
        observeAlarmRunningUseCase()
            .onEach { isAlarmRunning ->
                _state.update { it.copy(isAlarmRunning = isAlarmRunning) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeMonitoringState() {
        observeMonitoringStateUseCase()
            .onEach { monitoringState ->
                _state.update { it.copy(monitoringState = monitoringState) }
            }
            .launchIn(viewModelScope)
    }

    private fun stopAlarm() {
        viewModelScope.launch {
            runCatching {
                stopAlarmUseCase()
            }.onFailure { throwable ->
                Log.e(MAIN_VIEW_MODEL_TAG, "Failed to stop alarm", throwable)
            }
        }
    }

    private fun syncMonitoringRuntime() {
        viewModelScope.launch {
            runCatching {
                syncMonitoringRuntimeUseCase()
            }.onFailure { throwable ->
                Log.e(MAIN_VIEW_MODEL_TAG, "Failed to sync monitoring runtime", throwable)
            }
        }
    }

}
