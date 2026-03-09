package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val SECURITY_ICON_SIZE_DP = 150
private const val TOGGLE_BUTTON_MIN_WIDTH_DP = 160
private const val TOGGLE_BUTTON_WIDTH_FRACTION = 0.7f

@Composable
fun StatusBlock(
    modifier: Modifier = Modifier,
    monitoringState: MonitoringState,
    onToggle: () -> Unit,
) {
    val isActive = monitoringState == MonitoringState.ACTIVE

    val iconTint =
        if (isActive) VartovyiTheme.colors.primary
        else VartovyiTheme.colors.onSurfaceVariant

    val statusText =
        if (isActive) stringResource(R.string.monitoring_active)
        else stringResource(R.string.monitoring_inactive)

    val buttonText =
        if (isActive) stringResource(R.string.monitoring_deactivate)
        else stringResource(R.string.monitoring_activate)

    val buttonContainerColor =
        if (isActive) VartovyiTheme.colors.errorContainer
        else VartovyiTheme.colors.primary

    val buttonContentColor =
        if (isActive) VartovyiTheme.colors.onErrorContainer
        else VartovyiTheme.colors.onPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(VartovyiTheme.spacing.standard),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(
                if (isActive) R.drawable.security_on else R.drawable.security_off
            ),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(SECURITY_ICON_SIZE_DP.dp),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        Text(
            text = statusText,
            style = VartovyiTheme.typography.headlineSmall,
            color = VartovyiTheme.colors.onSurface,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.huge))

        Button(
            onClick = onToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonContainerColor,
                contentColor = buttonContentColor,
            ),
            modifier = Modifier
                .widthIn(min = TOGGLE_BUTTON_MIN_WIDTH_DP.dp)
                .fillMaxWidth(TOGGLE_BUTTON_WIDTH_FRACTION)
                .height(VartovyiTheme.spacing.massive),
        ) {
            Text(
                text = buttonText,
                style = VartovyiTheme.typography.titleMedium,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewStatusBlockActive() {
    VartovyiTheme {
        StatusBlock(
            monitoringState = MonitoringState.ACTIVE,
            onToggle = {},
        )
    }
}

@Preview
@Composable
private fun PreviewStatusBlockInactive() {
    VartovyiTheme {
        StatusBlock(
            monitoringState = MonitoringState.INACTIVE,
            onToggle = {},
        )
    }
}