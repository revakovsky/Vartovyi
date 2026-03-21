package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val HOME_CONTENT_ACTIVE_RING_CYCLE_DURATION_MS = 2700
private const val HOME_CONTENT_ACTIVE_RING_2_OFFSET_MS = 900
private const val HOME_CONTENT_ACTIVE_RING_3_OFFSET_MS = 1800
private const val HOME_CONTENT_ACTIVE_RING_BASE_FRACTION = 0.08f
private const val HOME_CONTENT_ACTIVE_RING_EXPAND_FRACTION = 0.42f
private const val HOME_CONTENT_ACTIVE_RING_MAX_ALPHA = 0.38f
private const val HOME_CONTENT_ACTIVE_RING_STROKE_WIDTH_DP = 1.5f

private data class HomeMonitoringActiveRingFrame(
    val ring1Progress: Float,
    val ring2Progress: Float,
    val ring3Progress: Float,
)

@Composable
private fun rememberHomeMonitoringActiveRingFrame(): HomeMonitoringActiveRingFrame {
    val infiniteTransition = rememberInfiniteTransition(
        label = "home_content_monitoring_active_rings",
    )

    val ring1Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = HOME_CONTENT_ACTIVE_RING_CYCLE_DURATION_MS,
                easing = LinearEasing,
            ),
            initialStartOffset = StartOffset(
                offsetMillis = 0,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "home_content_ring_1",
    )

    val ring2Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = HOME_CONTENT_ACTIVE_RING_CYCLE_DURATION_MS,
                easing = LinearEasing,
            ),
            initialStartOffset = StartOffset(
                offsetMillis = HOME_CONTENT_ACTIVE_RING_2_OFFSET_MS,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "home_content_ring_2",
    )

    val ring3Progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = HOME_CONTENT_ACTIVE_RING_CYCLE_DURATION_MS,
                easing = LinearEasing,
            ),
            initialStartOffset = StartOffset(
                offsetMillis = HOME_CONTENT_ACTIVE_RING_3_OFFSET_MS,
                offsetType = StartOffsetType.FastForward,
            ),
        ),
        label = "home_content_ring_3",
    )

    return HomeMonitoringActiveRingFrame(
        ring1Progress = ring1Progress,
        ring2Progress = ring2Progress,
        ring3Progress = ring3Progress,
    )
}

private fun DrawScope.drawHomeMonitoringActiveContentRings(
    frame: HomeMonitoringActiveRingFrame,
    ringCenter: Offset,
    ringColor: Color,
    strokeWidthPx: Float,
) {
    val baseRadius = size.minDimension * HOME_CONTENT_ACTIVE_RING_BASE_FRACTION

    listOf(
        frame.ring1Progress,
        frame.ring2Progress,
        frame.ring3Progress,
    ).forEach { progress ->
        val radius =
            baseRadius + progress * size.minDimension * HOME_CONTENT_ACTIVE_RING_EXPAND_FRACTION
        val alpha = (1f - progress).coerceIn(0f, 1f) * HOME_CONTENT_ACTIVE_RING_MAX_ALPHA

        drawCircle(
            color = ringColor.copy(alpha = alpha),
            radius = radius,
            center = ringCenter,
            style = Stroke(width = strokeWidthPx),
        )
    }
}

@Composable
fun HomeMonitoringActiveContentEffect(
    modifier: Modifier = Modifier,
    ringCenterInParent: Offset? = null,
) {
    val ringFrame = rememberHomeMonitoringActiveRingFrame()
    val ringColor = VartovyiTheme.colors.primary

    Canvas(modifier = modifier.fillMaxSize()) {
        val ringCenter = ringCenterInParent ?: center

        drawHomeMonitoringActiveContentRings(
            frame = ringFrame,
            ringCenter = ringCenter,
            ringColor = ringColor,
            strokeWidthPx = HOME_CONTENT_ACTIVE_RING_STROKE_WIDTH_DP.dp.toPx(),
        )
    }
}

@Preview
@Composable
private fun HomeMonitoringActiveContentEffectPreview() {
    VartovyiTheme {
        Box(modifier = Modifier.size(360.dp)) {
            HomeMonitoringActiveContentEffect(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
