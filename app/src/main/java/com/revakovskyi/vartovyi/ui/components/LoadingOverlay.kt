package com.revakovskyi.vartovyi.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val ICON_SIZE_DP = 88
private const val ICON_SCALE_MIN = 0.92f
private const val ICON_SCALE_MAX = 1.08f
private const val ICON_PULSE_DURATION_MS = 1200

private const val RING_CYCLE_DURATION_MS = 2400
private const val RING_2_OFFSET_MS = 800
private const val RING_3_OFFSET_MS = 1600
private const val RING_BASE_FRACTION = 0.12f
private const val RING_EXPAND_FRACTION = 0.35f
private const val RING_MAX_ALPHA = 0.5f
private const val RING_STROKE_WIDTH_DP = 1.5f

@Composable
fun LoadingOverlay() {

    val infiniteTransition = rememberInfiniteTransition(label = "loading_transition")

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

    val ringColor = VartovyiTheme.colors.primary

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasCenter = center
            val baseRadius = size.minDimension * RING_BASE_FRACTION

            listOf(ring1Progress, ring2Progress, ring3Progress).forEach { progress ->
                val radius = baseRadius + progress * size.minDimension * RING_EXPAND_FRACTION
                val alpha = (1f - progress).coerceIn(0f, 1f) * RING_MAX_ALPHA

                drawCircle(
                    color = ringColor.copy(alpha = alpha),
                    radius = radius,
                    center = canvasCenter,
                    style = Stroke(width = RING_STROKE_WIDTH_DP.dp.toPx()),
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.security_on),
                contentDescription = null,
                tint = VartovyiTheme.colors.primary,
                modifier = Modifier
                    .size(ICON_SIZE_DP.dp)
                    .scale(iconScale),
            )

            Text(
                text = stringResource(R.string.app_name),
                style = VartovyiTheme.typography.headlineSmall,
                color = VartovyiTheme.colors.onSurface,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewLoadingOverlay() {
    VartovyiTheme {
        LoadingOverlay()
    }
}
