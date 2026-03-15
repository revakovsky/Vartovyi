package com.revakovskyi.vartovyi.ui.screen.permissions.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsUiContract

internal data class PermissionItemUi(
    val title: String,
    val description: String,
    val isRequired: Boolean,
    val isGranted: Boolean,
    val onSwitchToggle: (isChecked: Boolean) -> PermissionsUiContract.Action,
)

@Composable
internal fun buildPermissionItems(
    state: PermissionsUiContract.State,
): List<PermissionItemUi> = listOf(
    PermissionItemUi(
        title = stringResource(R.string.permissions_listener_title),
        description = stringResource(R.string.permissions_listener_description),
        isRequired = true,
        isGranted = state.isNotificationListenerGranted,
        onSwitchToggle = { PermissionsUiContract.Action.RequestNotificationListenerAccess },
    ),
    PermissionItemUi(
        title = stringResource(R.string.permissions_battery_title),
        description = stringResource(R.string.permissions_battery_description),
        isRequired = true,
        isGranted = state.isBatteryOptimizationIgnored,
        onSwitchToggle = { shouldEnable ->
            PermissionsUiContract.Action.ToggleBatteryOptimizationPermission(shouldEnable)
        },
    ),
    PermissionItemUi(
        title = stringResource(R.string.permissions_dnd_title),
        description = stringResource(R.string.permissions_dnd_description),
        isRequired = false,
        isGranted = state.isDoNotDisturbAccessGranted,
        onSwitchToggle = { PermissionsUiContract.Action.RequestDoNotDisturbAccess },
    ),
    PermissionItemUi(
        title = stringResource(R.string.permissions_post_notifications_title),
        description = stringResource(R.string.permissions_post_notifications_description),
        isRequired = true,
        isGranted = state.isPostNotificationsGranted,
        onSwitchToggle = { PermissionsUiContract.Action.RequestPostNotificationsPermission },
    ),
    PermissionItemUi(
        title = stringResource(R.string.permissions_full_screen_title),
        description = stringResource(R.string.permissions_full_screen_description),
        isRequired = false,
        isGranted = state.isFullScreenIntentGranted,
        onSwitchToggle = { shouldEnable ->
            PermissionsUiContract.Action.ToggleFullScreenIntentPermission(shouldEnable)
        },
    ),
)
