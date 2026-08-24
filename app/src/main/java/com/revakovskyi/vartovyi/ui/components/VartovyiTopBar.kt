package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import com.revakovskyi.vartovyi.constants.SizeConstants.TOP_BAR_PERMISSION_BUTTON_SIZE
import com.revakovskyi.vartovyi.constants.SizeConstants.TOP_BAR_PERMISSION_ICON_SIZE
import com.revakovskyi.vartovyi.model.PermissionsStatus
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VartovyiTopBar(
    modifier: Modifier = Modifier,
    title: String,
    permissionsStatus: PermissionsStatus,
    isEmergencyStopVisible: Boolean,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    additionalActions: (@Composable () -> Unit)? = null,
    trailingActions: (@Composable () -> Unit)? = null,
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
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.none),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                additionalActions?.invoke()

                if (isEmergencyStopVisible) {
                    IconButton(
                        onClick = onEmergencyStopClick,
                        modifier = Modifier.size(TOP_BAR_PERMISSION_BUTTON_SIZE.dp),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.close),
                            contentDescription = stringResource(R.string.emergency_stop_content_description),
                            tint = VartovyiTheme.colors.error,
                            modifier = Modifier.size(TOP_BAR_PERMISSION_ICON_SIZE.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onPermissionsClick,
                    modifier = Modifier.size(TOP_BAR_PERMISSION_BUTTON_SIZE.dp),
                ) {
                    val icon = when (permissionsStatus) {
                        PermissionsStatus.MANDATORY_MISSING ->
                            ImageVector.vectorResource(R.drawable.security_red)

                        PermissionsStatus.RECOMMENDED_MISSING,
                        PermissionsStatus.GRANTED,
                            -> ImageVector.vectorResource(R.drawable.security_green)
                    }

                    val iconColor = when (permissionsStatus) {
                        PermissionsStatus.MANDATORY_MISSING -> VartovyiTheme.colors.error
                        PermissionsStatus.RECOMMENDED_MISSING -> VartovyiTheme.colors.secondary
                        PermissionsStatus.GRANTED -> VartovyiTheme.colors.primary
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(R.string.permissions_icon_content_description),
                        tint = iconColor,
                        modifier = Modifier.size(TOP_BAR_PERMISSION_ICON_SIZE.dp)
                    )
                }

                trailingActions?.invoke()
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Preview(name = "Permissions — all granted (green)")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VartovyiTopBarPermissionsGrantedPreview() {
    VartovyiTheme {
        VartovyiTopBar(
            title = "Вартовий",
            permissionsStatus = PermissionsStatus.GRANTED,
            isEmergencyStopVisible = true,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onPermissionsClick = {},
            onEmergencyStopClick = {},
        )
    }
}

@Preview(name = "Permissions — recommended missing (orange)")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VartovyiTopBarPermissionsRecommendedMissingPreview() {
    VartovyiTheme {
        VartovyiTopBar(
            title = "Вартовий",
            permissionsStatus = PermissionsStatus.RECOMMENDED_MISSING,
            isEmergencyStopVisible = false,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onPermissionsClick = {},
            onEmergencyStopClick = {},
        )
    }
}

@Preview(name = "Permissions — mandatory missing (red)")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VartovyiTopBarPermissionsMandatoryMissingPreview() {
    VartovyiTheme {
        VartovyiTopBar(
            title = "Вартовий",
            permissionsStatus = PermissionsStatus.MANDATORY_MISSING,
            isEmergencyStopVisible = false,
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
            onPermissionsClick = {},
            onEmergencyStopClick = {},
        )
    }
}
