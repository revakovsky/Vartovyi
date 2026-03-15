package com.revakovskyi.vartovyi.ui.screen.permissions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiSwitch
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun PermissionItemCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isGranted: Boolean,
    isRequired: Boolean,
    onAction: (action: PermissionsUiContract.Action) -> Unit,
    onSwitchToggle: (isChecked: Boolean) -> PermissionsUiContract.Action,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = title,
                        style = VartovyiTheme.typography.titleMedium,
                        color = VartovyiTheme.colors.onSurface,
                    )

                    val statusColor =
                        if (isGranted) VartovyiTheme.colors.primary
                        else VartovyiTheme.colors.error

                    val statusText =
                        if (isGranted) stringResource(R.string.permissions_status_granted)
                        else stringResource(R.string.permissions_status_missing)

                    Text(
                        text = statusText,
                        style = VartovyiTheme.typography.labelMedium,
                        color = statusColor,
                    )
                }

                VartovyiSwitch(
                    checked = isGranted,
                    onCheckedChange = { isChecked ->
                        onAction(onSwitchToggle(isChecked))
                    },
                )
            }

            Text(
                text = description,
                style = VartovyiTheme.typography.bodySmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )

            Text(
                text = if (isRequired) {
                    stringResource(R.string.permissions_required)
                } else {
                    stringResource(R.string.permissions_recommended)
                },
                style = VartovyiTheme.typography.labelSmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Permission item — granted")
@Composable
private fun PreviewPermissionGrantedItemCard() {
    VartovyiTheme {
        PermissionItemCard(
            title = "Test permisiion",
            description = "Test description for test permission",
            isGranted = true,
            isRequired = true,
            onAction = {},
            onSwitchToggle = { _ ->
                PermissionsUiContract.Action.RequestPostNotificationsPermission
            },
        )
    }
}

@Preview(name = "Permission item — not granted")
@Composable
private fun PreviewPermissionNotGrantedItemCard() {
    VartovyiTheme {
        PermissionItemCard(
            title = "Test permisiion",
            description = "Test description for test permission",
            isGranted = false,
            isRequired = false,
            onAction = {},
            onSwitchToggle = { _ ->
                PermissionsUiContract.Action.RequestPostNotificationsPermission
            },
        )
    }
}

@Preview(name = "Permission item — granted with long text")
@Composable
private fun PreviewPermissionGrantedItemCardWithLongText() {
    VartovyiTheme {
        PermissionItemCard(
            title = "Test permisiion very very very very very very very very very very very very very long",
            description = "Test description for test permission description for test permission description for test permission description for test permission description for test permission",
            isGranted = true,
            isRequired = false,
            onAction = {},
            onSwitchToggle = { _ ->
                PermissionsUiContract.Action.RequestPostNotificationsPermission
            },
        )
    }
}
