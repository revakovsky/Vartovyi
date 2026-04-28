package com.revakovskyi.vartovyi.ui

import com.revakovskyi.vartovyi.model.MonitoringState

object MainUiContract {

    data class State(
        val isAlarmRunning: Boolean = false,
        val monitoringState: MonitoringState = MonitoringState.INACTIVE,
    )

    sealed interface Action {
        data object StopAlarm : Action
        data object SyncMonitoringRuntime : Action
    }

    sealed interface Event

}
