package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.runtime.Immutable
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.MonitoringState

interface HomeUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val monitoringState: MonitoringState = MonitoringState.INACTIVE,
        val isScheduleEnabled: Boolean = false,
        val startTime: String = "22:00",
        val endTime: String = "07:00",
        val lastAlertEvent: AlertEvent? = null,
        val isListenerServiceActive: Boolean = false,
        val keywords: List<String> = emptyList(),
        val isAlarmRunning: Boolean = false,
    )

    sealed interface Action {
        data object ToggleMonitoring : Action
        data object NavigateToKeywords : Action
        data object NavigateToLog : Action
    }

    sealed interface Event {
        data object NavigateToKeywords : Event
        data object NavigateToLog : Event
    }

}
