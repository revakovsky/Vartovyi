package com.revakovskyi.vartovyi.ui.screen.permissions

import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionsViewModel : ViewModel() {

    private val _state = MutableStateFlow(PermissionsUiContract.State())
    val state: StateFlow<PermissionsUiContract.State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<PermissionsUiContract.Event>()
    val events: SharedFlow<PermissionsUiContract.Event> = _events.asSharedFlow()

    fun onAction(action: PermissionsUiContract.Action) {
        when (action) {
            is PermissionsUiContract.Action.RequestNotificationListenerAccess -> requestListenerAccess()
            is PermissionsUiContract.Action.RequestPostNotificationsPermission -> requestPostNotifications()
            is PermissionsUiContract.Action.RequestFullScreenIntentPermission -> requestFullScreenIntent()
            is PermissionsUiContract.Action.CheckPermissions -> checkPermissions()
            is PermissionsUiContract.Action.NavigateBack -> navigateBack()
        }
    }

    fun updatePermissionsState(
        listenerGranted: Boolean,
        postNotificationsGranted: Boolean,
        vibrateGranted: Boolean,
        fullScreenIntentGranted: Boolean,
    ) {
        val allGranted = listenerGranted && postNotificationsGranted && fullScreenIntentGranted

        _state.update {
            it.copy(
                isNotificationListenerGranted = listenerGranted,
                isPostNotificationsGranted = postNotificationsGranted,
                isVibrateGranted = vibrateGranted,
                isFullScreenIntentGranted = fullScreenIntentGranted,
                allGranted = allGranted,
            )
        }

        if (allGranted) {
            viewModelScope.launch { _events.emit(PermissionsUiContract.Event.AllPermissionsGranted) }
        }
    }

    private fun requestListenerAccess() {
        viewModelScope.launch {
            _events.emit(
                PermissionsUiContract.Event.NavigateToSystemSettings(
                    Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
                )
            )
        }
    }

    private fun requestPostNotifications() {
        viewModelScope.launch {
            _events.emit(
                PermissionsUiContract.Event.NavigateToSystemSettings(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                )
            )
        }
    }

    private fun requestFullScreenIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            viewModelScope.launch {
                _events.emit(
                    PermissionsUiContract.Event.NavigateToSystemSettings(
                        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
                    )
                )
            }
        }
    }

    private fun checkPermissions() = Unit

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(PermissionsUiContract.Event.NavigateBack) }
    }

}
