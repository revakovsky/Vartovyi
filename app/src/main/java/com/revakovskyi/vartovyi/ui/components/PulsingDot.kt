package com.revakovskyi.vartovyi.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val PULSING_DOT_SCALE_MIN = 1f
private const val PULSING_DOT_SCALE_MAX = 1.3f
private const val PULSING_DOT_ALPHA_MIN = 0.6f
private const val PULSING_DOT_ALPHA_MAX = 1f
private const val PULSING_DOT_DURATION_MS = 900

@Composable
fun PulsingDot(
    modifier: Modifier = Modifier,
    color: Color,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing-dot")

    val scale by infiniteTransition.animateFloat(
        initialValue = PULSING_DOT_SCALE_MIN,
        targetValue = PULSING_DOT_SCALE_MAX,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = PULSING_DOT_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulsing-dot-scale",
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = PULSING_DOT_ALPHA_MAX,
        targetValue = PULSING_DOT_ALPHA_MIN,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = PULSING_DOT_DURATION_MS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulsing-dot-alpha",
    )

    Box(
        modifier = modifier
            .size(VartovyiTheme.spacing.small)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(
                color = color,
                shape = CircleShape,
            ),
    )
}

@Preview
@Composable
private fun PulsingDotPreview() {
    VartovyiTheme {
        PulsingDot(color = VartovyiTheme.colors.error)
    }
}
