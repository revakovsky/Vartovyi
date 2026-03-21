package com.revakovskyi.vartovyi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

private const val ICON_SCALE_MIN = 0.92f
private const val ICON_SCALE_MAX = 1.08f
private const val ICON_PULSE_DURATION_MS = 1200

private const val RING_CYCLE_DURATION_MS = 2400
private const val RING_2_OFFSET_MS = 800
private const val RING_3_OFFSET_MS = 1600
private const val RING_BASE_FRACTION = 0.12f
private const val RING_EXPAND_FRACTION = 0.35f
private const val RING_MAX_ALPHA = 0.5f
internal const val MONITORING_ACTIVE_SIGNAL_RING_STROKE_WIDTH_DP = 1.5f

internal data class MonitoringActiveIconSignalsFrame(
    val iconScale: Float,
    val ring1Progress: Float,
    val ring2Progress: Float,
    val ring3Progress: Float,
)

@Composable
internal fun rememberMonitoringActiveIconSignalsFrame(
    infiniteTransitionLabel: String,
): MonitoringActiveIconSignalsFrame {
    val infiniteTransition = rememberInfiniteTransition(label = infiniteTransitionLabel)

    val iconScale by infiniteTransition.animateFloat(
        initialValue = ICON_SCALE_MIN,
        targetValue = ICON_SCALE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ICON_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "icon_scale",
    )

    val ring1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = RING_CYCLE_DURATION_MS, easing = LinearEasing),
            initialStartOffset = StartOffset(
                offsetMillis = 0,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "ring1_progress",
    )

    val ring2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = RING_CYCLE_DURATION_MS, easing = LinearEasing),
            initialStartOffset = StartOffset(
                offsetMillis = RING_2_OFFSET_MS,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "ring2_progress",
    )

    val ring3Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = RING_CYCLE_DURATION_MS, easing = LinearEasing),
            initialStartOffset = StartOffset(
                offsetMillis = RING_3_OFFSET_MS,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "ring3_progress",
    )

    return MonitoringActiveIconSignalsFrame(
        iconScale = iconScale,
        ring1Progress = ring1Progress,
        ring2Progress = ring2Progress,
        ring3Progress = ring3Progress,
    )
}

internal fun DrawScope.drawMonitoringActiveIconSignalRings(
    frame: MonitoringActiveIconSignalsFrame,
    ringColor: Color,
    strokeWidthPx: Float,
) {
    val canvasCenter = center
    val baseRadius = size.minDimension * RING_BASE_FRACTION

    listOf(
        frame.ring1Progress,
        frame.ring2Progress,
        frame.ring3Progress,
    ).forEach { progress ->
        val radius = baseRadius + progress * size.minDimension * RING_EXPAND_FRACTION
        val alpha = (1f - progress).coerceIn(0f, 1f) * RING_MAX_ALPHA

        drawCircle(
            color = ringColor.copy(alpha = alpha),
            radius = radius,
            center = canvasCenter,
            style = Stroke(width = strokeWidthPx),
        )
    }
}
