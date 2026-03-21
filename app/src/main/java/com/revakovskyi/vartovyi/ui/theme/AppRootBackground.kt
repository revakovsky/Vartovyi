package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import kotlin.math.hypot
import kotlin.math.min
import androidx.compose.ui.util.lerp as lerpFloat

fun Modifier.appRootBackground(monitoringState: MonitoringState): Modifier =
    composed {
        val colorScheme = VartovyiTheme.colors

        val transition = updateTransition(
            targetState = monitoringState,
            label = "appRootBackground",
        )

        val blend by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = 750,
                    easing = FastOutSlowInEasing,
                )
            },
            label = "gradientBlend",
        ) { state ->
            when (state) {
                MonitoringState.ACTIVE -> 1f
                MonitoringState.INACTIVE,
                MonitoringState.SCHEDULED,
                    -> 0f
            }
        }

        drawBehind {
            val background = colorScheme.background

            val inactiveCenter = Offset(
                x = size.width * 0.5f,
                y = size.height,
            )
            val inactiveRadius = hypot(
                x = size.width * 0.5f,
                y = size.height,
            ) * 1.08f

            val inactiveInner = lerp(
                start = background,
                stop = colorScheme.tertiary,
                fraction = 0.32f,
            )
            val inactiveMid = lerp(
                start = background,
                stop = colorScheme.surfaceVariant,
                fraction = 0.62f,
            )
            val inactiveOuter = background

            val activeCenter = Offset(
                x = size.width * 0.5f,
                y = size.height * 0.5f,
            )
            val activeRadius = min(
                a = size.width,
                b = size.height,
            ) * 0.52f

            val activeInner = lerp(
                start = background,
                stop = colorScheme.primary,
                fraction = 0.2f,
            )
            val activeMid = lerp(
                start = background,
                stop = colorScheme.primaryContainer,
                fraction = 0.52f,
            )
            val activeOuter = lerp(
                start = background,
                stop = colorScheme.primary,
                fraction = 0.11f,
            )

            val center = Offset(
                x = lerpFloat(
                    start = inactiveCenter.x,
                    stop = activeCenter.x,
                    fraction = blend,
                ),
                y = lerpFloat(
                    start = inactiveCenter.y,
                    stop = activeCenter.y,
                    fraction = blend,
                ),
            )
            val radius = lerpFloat(
                start = inactiveRadius,
                stop = activeRadius,
                fraction = blend,
            )

            val inner = lerp(
                start = inactiveInner,
                stop = activeInner,
                fraction = blend,
            )
            val mid = lerp(
                start = inactiveMid,
                stop = activeMid,
                fraction = blend,
            )
            val outer = lerp(
                start = inactiveOuter,
                stop = activeOuter,
                fraction = blend,
            )

            val brush = Brush.radialGradient(
                colors = listOf(inner, mid, outer),
                center = center,
                radius = radius,
            )

            drawRect(brush = brush)
        }
    }
