package com.revakovskyi.vartovyi.ui.screen.permissions

import androidx.compose.runtime.Immutable

interface PermissionsUiContract {

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val allGranted: Boolean = false,
        val isNotificationListenerGranted: Boolean = false,
        val isBatteryOptimizationIgnored: Boolean = false,
        val isDoNotDisturbAccessGranted: Boolean = false,
        val isPostNotificationsGranted: Boolean = false,
        val isFullScreenIntentGranted: Boolean = false,
        val hasMissingPermissions: Boolean = true,
    )

    sealed interface Action {
        data object RequestNotificationListenerAccess : Action
        data class ToggleBatteryOptimizationPermission(val shouldEnable: Boolean) : Action
        data object RequestDoNotDisturbAccess : Action
        data object RequestPostNotificationsPermission : Action
        data class ToggleFullScreenIntentPermission(val shouldEnable: Boolean) : Action
        data object CheckPermissions : Action
        data object NavigateBack : Action
    }

    sealed interface Event {
        data object NavigateBack : Event
        data class NavigateToSystemSettings(val action: String) : Event
        data object RefreshPermissionsState : Event
    }

}
