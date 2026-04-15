package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private val TOP_BAR_PERMISSION_ICON_SIZE = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VartovyiTopBar(
    modifier: Modifier = Modifier,
    title: String,
    hasMissingPermissions: Boolean,
    isEmergencyStopVisible: Boolean,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    additionalActions: (@Composable () -> Unit)? = null,
    onPermissionsClick: () -> Unit,
    onEmergencyStopClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = VartovyiTheme.typography.titleLarge,
                color = VartovyiTheme.colors.onBackground,
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                additionalActions?.invoke()

                if (isEmergencyStopVisible) {
                    TopBarTooltipIconButton(
                        tooltipText = stringResource(R.string.emergency_stop_content_description),
                        onClick = onEmergencyStopClick,
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.close),
                            contentDescription = stringResource(R.string.emergency_stop_content_description),
                            tint = VartovyiTheme.colors.error,
                            modifier = Modifier.size(TOP_BAR_PERMISSION_ICON_SIZE)
                        )
                    }
                }

                TopBarTooltipIconButton(
                    tooltipText = stringResource(R.string.permissions_icon_content_description),
                    onClick = onPermissionsClick,
                ) {
                    val icon =
                        if (hasMissingPermissions) ImageVector.vectorResource(R.drawable.security_red)
                        else ImageVector.vectorResource(R.drawable.security_green)

                    val iconColor =
                        if (hasMissingPermissions) VartovyiTheme.colors.error
                        else VartovyiTheme.colors.primary

                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(R.string.permissions_icon_content_description),
                        tint = iconColor,
                        modifier = Modifier.size(TOP_BAR_PERMISSION_ICON_SIZE)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarTooltipIconButton(
    tooltipText: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                Text(text = tooltipText)
            }
        },
        state = rememberTooltipState(),
    ) {
        IconButton(onClick = onClick) {
            content()
        }
    }
}

@Preview(name = "All permissions were granted")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VartovyiTopBarPermissionsGrantedPreview() {
    VartovyiTheme {
        VartovyiTopBar(
            title = "Вартовий",
            hasMissingPermissions = false,
            isEmergencyStopVisible = true,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onPermissionsClick = {},
            onEmergencyStopClick = {},
        )
    }
}

@Preview(name = "Not all permissions were granted")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VartovyiTopBarNotAllPermissionsGrantedPreview() {
    VartovyiTheme {
        VartovyiTopBar(
            title = "Вартовий",
            hasMissingPermissions = true,
            isEmergencyStopVisible = false,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onPermissionsClick = {},
            onEmergencyStopClick = {},
        )
    }
}
