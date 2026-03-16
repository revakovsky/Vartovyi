package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ICON_SIZE_FRACTION = 0.5f
private const val ICON_MIN_SIZE_DP = 32
private const val ICON_MAX_SIZE_DP = 200
private const val SPACER_BOTTOM_WEIGHT = 0.5f
private const val TOGGLE_BUTTON_MIN_WIDTH_DP = 160
private const val TOGGLE_BUTTON_WIDTH_FRACTION = 0.7f
private const val MILLIS_IN_SECOND = 1000L
private const val SECONDS_IN_MINUTE = 60L

@Composable
fun StatusBlock(
    modifier: Modifier = Modifier,
    monitoringState: MonitoringState,
    alarmRetriggerCooldownMillis: Long,
    onToggle: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

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

    val formattedCooldownTime = remember(isActive, alarmRetriggerCooldownMillis) {
        if (isActive && alarmRetriggerCooldownMillis > 0L) {
            val seconds = alarmRetriggerCooldownMillis / MILLIS_IN_SECOND
            val minutesPart = seconds / SECONDS_IN_MINUTE
            val secondsPart = seconds % SECONDS_IN_MINUTE
            "%02d:%02d".format(minutesPart, secondsPart)
        } else {
            ""
        }
    }

    val cooldownText = stringResource(
        id = R.string.home_alarm_retrigger_cooldown, formattedCooldownTime,
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val iconSize = (maxHeight * ICON_SIZE_FRACTION)
            .coerceIn(ICON_MIN_SIZE_DP.dp, ICON_MAX_SIZE_DP.dp)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = ImageVector.vectorResource(
                    if (isActive) R.drawable.security_on
                    else R.drawable.security_off
                ),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(iconSize),
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            Text(
                text = statusText,
                style = VartovyiTheme.typography.headlineSmall,
                color = VartovyiTheme.colors.onSurface,
            )

            if (cooldownText.isNotEmpty() && alarmRetriggerCooldownMillis > 0) {
                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

                Text(
                    text = cooldownText,
                    style = VartovyiTheme.typography.bodySmall,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.weight(SPACER_BOTTOM_WEIGHT))

            Button(
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onToggle()
                },
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

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun PreviewStatusBlockActive() {
    VartovyiTheme {
        StatusBlock(
            monitoringState = MonitoringState.ACTIVE,
            alarmRetriggerCooldownMillis = 143_000L,
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
            alarmRetriggerCooldownMillis = 0L,
            onToggle = {},
        )
    }
}
