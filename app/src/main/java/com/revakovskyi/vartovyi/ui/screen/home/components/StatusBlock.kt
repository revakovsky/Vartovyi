package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

private const val ICON_SIZE_FRACTION = 0.42f
private const val ICON_MIN_SIZE_DP = 32
private const val ICON_MAX_SIZE_DP = 200
private const val SPACER_BOTTOM_WEIGHT = 1f
private const val TOGGLE_BUTTON_MIN_WIDTH_DP = 160
private const val TOGGLE_BUTTON_MAX_WIDTH_DP = 450
private const val TOGGLE_BUTTON_WIDTH_FRACTION = 0.7f
private const val TOGGLE_BUTTON_CORNER_PERCENT = 50
private const val MILLIS_IN_SECOND = 1000L
private const val SECONDS_IN_MINUTE = 60L
private const val ACTIVATE_BUTTON_ENTRY_DELAY_MILLIS = 500L
private const val ACTIVATE_BUTTON_PULSE_SCALE_PEAK = 1.035f
private const val ACTIVATE_BUTTON_PULSE_EXPAND_DURATION_MILLIS = 900
private const val ACTIVATE_BUTTON_PULSE_CONTRACT_DURATION_MILLIS = 900
private const val ACTIVATE_BUTTON_PULSE_SETTLE_DURATION_MILLIS = 350
private const val MONITORING_ACTIVE_ICON_SCALE_MIN = 0.93f
private const val MONITORING_ACTIVE_ICON_SCALE_MAX = 1.09f
private const val MONITORING_ACTIVE_ICON_PULSE_DURATION_MS = 1350

@Composable
fun StatusBlock(
    modifier: Modifier = Modifier,
    monitoringState: MonitoringState,
    alarmRetriggerCooldownMillis: Long,
    onToggle: () -> Unit,
    homeContentLayoutCoordinates: () -> LayoutCoordinates? = { null },
    onSecurityIconCenterInHomeContentChanged: (Offset) -> Unit = { },
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
        id = R.string.home_alarm_retrigger_cooldown,
        formattedCooldownTime,
    )

    val activateButtonPulseScale = remember { Animatable(1f) }

    val currentMonitoringState by rememberUpdatedState(monitoringState)

    val monitoringIconPulseTransition = rememberInfiniteTransition(
        label = "status_security_icon_active_pulse",
    )

    val monitoringIconPulseScale by monitoringIconPulseTransition.animateFloat(
        initialValue = MONITORING_ACTIVE_ICON_SCALE_MIN,
        targetValue = MONITORING_ACTIVE_ICON_SCALE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = MONITORING_ACTIVE_ICON_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "monitoring_icon_pulse_scale",
    )

    val securityIconScale = if (isActive) monitoringIconPulseScale else 1f

    LaunchedEffect(Unit) {
        delay(ACTIVATE_BUTTON_ENTRY_DELAY_MILLIS)

        val motionScale = coroutineContext[MotionDurationScale]?.scaleFactor ?: 1f
        if (motionScale <= 0f) {
            activateButtonPulseScale.snapTo(1f)
            return@LaunchedEffect
        }

        snapshotFlow { currentMonitoringState == MonitoringState.ACTIVE }
            .collectLatest { active ->
                if (active) {
                    activateButtonPulseScale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(
                            durationMillis = ACTIVATE_BUTTON_PULSE_SETTLE_DURATION_MILLIS,
                            easing = FastOutSlowInEasing,
                        ),
                    )
                } else {
                    while (true) {
                        activateButtonPulseScale.animateTo(
                            targetValue = ACTIVATE_BUTTON_PULSE_SCALE_PEAK,
                            animationSpec = tween(
                                durationMillis = ACTIVATE_BUTTON_PULSE_EXPAND_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )

                        activateButtonPulseScale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = ACTIVATE_BUTTON_PULSE_CONTRACT_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )
                    }
                }
            }
    }

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
                modifier = Modifier
                    .size(iconSize)
                    .onGloballyPositioned { iconCoordinates ->
                        val homeContentCoordinates = homeContentLayoutCoordinates()
                        if (
                            homeContentCoordinates == null ||
                            !homeContentCoordinates.isAttached ||
                            !iconCoordinates.isAttached
                        ) {
                            return@onGloballyPositioned
                        }

                        val topLeftInHomeContent = homeContentCoordinates.localPositionOf(
                            sourceCoordinates = iconCoordinates,
                            relativeToSource = Offset.Zero,
                        )
                        val iconCenterInHomeContent = topLeftInHomeContent + Offset(
                            x = iconCoordinates.size.width / 2f,
                            y = iconCoordinates.size.height / 2f,
                        )

                        onSecurityIconCenterInHomeContentChanged(iconCenterInHomeContent)
                    }
                    .graphicsLayer {
                        scaleX = securityIconScale
                        scaleY = securityIconScale
                    }
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

            MonitoringToggleButton(
                text = buttonText,
                containerColor = buttonContainerColor,
                contentColor = buttonContentColor,
                pulseScale = { activateButtonPulseScale.value },
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onToggle()
                },
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun MonitoringToggleButton(
    modifier: Modifier = Modifier,
    text: String,
    containerColor: Color,
    contentColor: Color,
    pulseScale: () -> Float,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(percent = TOGGLE_BUTTON_CORNER_PERCENT)

    val buttonHeight: Dp = VartovyiTheme.spacing.massive

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(
                min = TOGGLE_BUTTON_MIN_WIDTH_DP.dp,
                max = TOGGLE_BUTTON_MAX_WIDTH_DP.dp,
            )
            .fillMaxWidth(TOGGLE_BUTTON_WIDTH_FRACTION)
            .padding(horizontal = VartovyiTheme.spacing.small)
            .height(buttonHeight)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    val scale = pulseScale()
                    scaleX = scale
                    scaleY = scale
                }
                .clip(shape)
                .background(color = containerColor)
                .clickable(onClick = onClick)
        )

        Text(
            text = text,
            style = VartovyiTheme.typography.titleMedium,
            color = contentColor,
        )
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
