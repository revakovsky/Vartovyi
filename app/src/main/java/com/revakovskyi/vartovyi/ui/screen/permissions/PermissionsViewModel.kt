package com.revakovskyi.vartovyi.ui.screen.permissions

import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsUiContract.Action
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsUiContract.Event
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsUiContract.State
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionsViewModel : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.RequestNotificationListenerAccess -> requestListenerAccess()
            is Action.RequestDoNotDisturbAccess -> requestDoNotDisturbAccess()
            is Action.RequestPostNotificationsPermission -> requestPostNotifications()
            is Action.ToggleFullScreenIntentPermission -> toggleFullScreenIntentPermission(action.shouldEnable)
            is Action.CheckPermissions -> checkPermissions()
            is Action.NavigateBack -> navigateBack()
            is Action.ToggleBatteryOptimizationPermission -> {
                toggleBatteryOptimizationPermission(action.shouldEnable)
            }
        }
    }

    fun updatePermissionsState(
        listenerGranted: Boolean,
        batteryOptimizationIgnored: Boolean,
        doNotDisturbAccessGranted: Boolean,
        postNotificationsGranted: Boolean,
        fullScreenIntentGranted: Boolean,
    ) {
        val allGranted = listenerGranted && batteryOptimizationIgnored && postNotificationsGranted
        val hasMissingPermissions = !allGranted ||
                !doNotDisturbAccessGranted || !fullScreenIntentGranted

        _state.update {
            it.copy(
                isLoading = false,
                allGranted = allGranted,
                isNotificationListenerGranted = listenerGranted,
                isBatteryOptimizationIgnored = batteryOptimizationIgnored,
                isDoNotDisturbAccessGranted = doNotDisturbAccessGranted,
                isPostNotificationsGranted = postNotificationsGranted,
                isFullScreenIntentGranted = fullScreenIntentGranted,
                hasMissingPermissions = hasMissingPermissions,
            )
        }
    }

    private fun requestListenerAccess() {
        viewModelScope.launch {
            _events.emit(Event.NavigateToSystemSettings(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
    }

    private fun requestPostNotifications() {
        viewModelScope.launch {
            _events.emit(
                Event.NavigateToSystemSettings(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                )
            )
        }
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationIgnore() {
        viewModelScope.launch {
            _events.emit(
                Event.NavigateToSystemSettings(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                )
            )
        }
    }

    private fun requestDoNotDisturbAccess() {
        viewModelScope.launch {
            _events.emit(
                Event.NavigateToSystemSettings(
                    Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                )
            )
        }
    }

    private fun toggleBatteryOptimizationPermission(shouldEnable: Boolean) {
        if (shouldEnable) {
            requestBatteryOptimizationIgnore()
            return
        }
        openAppDetailsSettings()
    }

    private fun requestFullScreenIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            viewModelScope.launch {
                _events.emit(
                    Event.NavigateToSystemSettings(
                        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
                    )
                )
            }
        }
    }

    private fun toggleFullScreenIntentPermission(shouldEnable: Boolean) {
        if (shouldEnable) {
            requestFullScreenIntent()
            return
        }
        openAppNotificationSettings()
    }

    private fun openAppDetailsSettings() {
        viewModelScope.launch {
            _events.emit(
                Event.NavigateToSystemSettings(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                )
            )
        }
    }

    private fun openAppNotificationSettings() {
        viewModelScope.launch {
            _events.emit(
                Event.NavigateToSystemSettings(
                    Settings.ACTION_APP_NOTIFICATION_SETTINGS
                )
            )
        }
    }

    private fun checkPermissions() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch { _events.emit(Event.RefreshPermissionsState) }
    }

    private fun navigateBack() {
        viewModelScope.launch { _events.emit(Event.NavigateBack) }
    }

}
