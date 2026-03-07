package com.revakovskyi.vartovyi.ui.screen.permissions

import androidx.compose.runtime.Immutable

interface PermissionsUiContract {

    @Immutable
    data class State(
        val isNotificationListenerGranted: Boolean = false,
        val isPostNotificationsGranted: Boolean = false,
        val isVibrateGranted: Boolean = false,
        val isFullScreenIntentGranted: Boolean = false,
        val allGranted: Boolean = false,
    )

    sealed interface Action {
        data object RequestNotificationListenerAccess : Action
        data object RequestPostNotificationsPermission : Action
        data object RequestFullScreenIntentPermission : Action
        data object CheckPermissions : Action
        data object NavigateBack : Action
    }

    sealed interface Event {
        data object AllPermissionsGranted : Event
        data object NavigateBack : Event
        data class NavigateToSystemSettings(val action: String) : Event
    }

}
