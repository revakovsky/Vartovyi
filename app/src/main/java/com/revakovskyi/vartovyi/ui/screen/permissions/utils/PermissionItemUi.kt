package com.revakovskyi.vartovyi.ui.screen.permissions.utils

import android.os.Build
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
): List<PermissionItemUi> = buildList {
    add(
        PermissionItemUi(
            title = stringResource(R.string.permissions_listener_title),
            description = stringResource(R.string.permissions_listener_description),
            isRequired = true,
            isGranted = state.isNotificationListenerGranted,
            onSwitchToggle = { PermissionsUiContract.Action.RequestNotificationListenerAccess },
        )
    )

    add(
        PermissionItemUi(
            title = stringResource(R.string.permissions_battery_title),
            description = stringResource(R.string.permissions_battery_description),
            isRequired = true,
            isGranted = state.isBatteryOptimizationIgnored,
            onSwitchToggle = { shouldEnable ->
                PermissionsUiContract.Action.ToggleBatteryOptimizationPermission(shouldEnable)
            },
        )
    )

    add(
        PermissionItemUi(
            title = stringResource(R.string.permissions_dnd_title),
            description = stringResource(R.string.permissions_dnd_description),
            isRequired = false,
            isGranted = state.isDoNotDisturbAccessGranted,
            onSwitchToggle = { PermissionsUiContract.Action.RequestDoNotDisturbAccess },
        )
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        add(
            PermissionItemUi(
                title = stringResource(R.string.permissions_post_notifications_title),
                description = stringResource(R.string.permissions_post_notifications_description),
                isRequired = true,
                isGranted = state.isPostNotificationsGranted,
                onSwitchToggle = { PermissionsUiContract.Action.RequestPostNotificationsPermission },
            )
        )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        add(
            PermissionItemUi(
                title = stringResource(R.string.permissions_full_screen_title),
                description = stringResource(R.string.permissions_full_screen_description),
                isRequired = false,
                isGranted = state.isFullScreenIntentGranted,
                onSwitchToggle = { PermissionsUiContract.Action.ToggleFullScreenIntentPermission },
            )
        )
    }
}
