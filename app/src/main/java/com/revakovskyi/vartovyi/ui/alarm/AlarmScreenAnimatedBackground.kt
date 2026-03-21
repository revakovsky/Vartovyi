package com.revakovskyi.vartovyi.ui.alarm

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ALARM_BG_VIGNETTE_PULSE_MIN = 0.12f
private const val ALARM_BG_VIGNETTE_PULSE_MAX = 0.28f
private const val ALARM_BG_VIGNETTE_PULSE_DURATION_MS = 720

private const val ALARM_BG_RING_CYCLE_DURATION_MS = 1900
private const val ALARM_BG_RING_2_OFFSET_MS = 950
private const val ALARM_BG_RING_BASE_FRACTION = 0.06f
private const val ALARM_BG_RING_EXPAND_FRACTION = 0.44f
private const val ALARM_BG_RING_MAX_ALPHA = 0.42f
private const val ALARM_BG_RING_STROKE_WIDTH_DP = 2f

private const val ALARM_BG_RADIAL_CENTER_Y_FRACTION = 0.36f
private const val ALARM_BG_RADIAL_RADIUS_FRACTION = 0.72f

private data class AlarmBackgroundRingFrame(
    val ring1Progress: Float,
    val ring2Progress: Float,
)

@Composable
fun AlarmScreenAnimatedBackground(
    modifier: Modifier = Modifier,
    effectCenterInParent: Offset? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "alarm_screen_background")

    val vignetteStrength by infiniteTransition.animateFloat(
        initialValue = ALARM_BG_VIGNETTE_PULSE_MIN,
        targetValue = ALARM_BG_VIGNETTE_PULSE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = ALARM_BG_VIGNETTE_PULSE_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alarm_vignette_pulse",
    )

    val ring1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ALARM_BG_RING_CYCLE_DURATION_MS,
                easing = LinearEasing,
            ),
            initialStartOffset = StartOffset(
                offsetMillis = 0,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "alarm_ring_1",
    )

    val ring2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = ALARM_BG_RING_CYCLE_DURATION_MS,
                easing = LinearEasing,
            ),
            initialStartOffset = StartOffset(
                offsetMillis = ALARM_BG_RING_2_OFFSET_MS,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "alarm_ring_2",
    )

    val backgroundColor = VartovyiTheme.colors.background
    val errorColor = VartovyiTheme.colors.error

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = backgroundColor)

        val radialCenter = effectCenterInParent ?: Offset(
            x = size.width * 0.5f,
            y = size.height * ALARM_BG_RADIAL_CENTER_Y_FRACTION,
        )
        val radialRadius = size.maxDimension * ALARM_BG_RADIAL_RADIUS_FRACTION
        val midTint = lerp(
            start = backgroundColor,
            stop = errorColor,
            fraction = vignetteStrength,
        )
        val edgeTint = lerp(
            start = backgroundColor,
            stop = errorColor,
            fraction = vignetteStrength * 0.55f,
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    midTint,
                    edgeTint,
                    backgroundColor,
                ),
                center = radialCenter,
                radius = radialRadius,
            ),
            radius = radialRadius,
            center = radialCenter,
        )

        val ringFrame = AlarmBackgroundRingFrame(
            ring1Progress = ring1Progress,
            ring2Progress = ring2Progress,
        )
        drawAlarmBackgroundRings(
            frame = ringFrame,
            ringCenter = radialCenter,
            ringColor = errorColor,
            strokeWidthPx = ALARM_BG_RING_STROKE_WIDTH_DP.dp.toPx(),
        )
    }
}

private fun DrawScope.drawAlarmBackgroundRings(
    frame: AlarmBackgroundRingFrame,
    ringCenter: Offset,
    ringColor: Color,
    strokeWidthPx: Float,
) {
    val baseRadius = size.minDimension * ALARM_BG_RING_BASE_FRACTION

    listOf(
        frame.ring1Progress,
        frame.ring2Progress,
    ).forEach { progress ->
        val radius = baseRadius + progress * size.minDimension * ALARM_BG_RING_EXPAND_FRACTION
        val alpha = (1f - progress).coerceIn(0f, 1f) * ALARM_BG_RING_MAX_ALPHA

        drawCircle(
            color = ringColor.copy(alpha = alpha),
            radius = radius,
            center = ringCenter,
            style = Stroke(width = strokeWidthPx),
        )
    }
}

@Preview(
    name = "Alarm animated background",
    widthDp = 360,
    heightDp = 640,
    showBackground = false,
)
@Composable
private fun AlarmScreenAnimatedBackgroundPreview() {
    VartovyiTheme {
        Box(modifier = Modifier.size(360.dp, 640.dp)) {
            AlarmScreenAnimatedBackground(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
