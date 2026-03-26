package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import com.revakovskyi.vartovyi.model.MonitoringState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.min
import kotlin.random.Random
import androidx.compose.ui.util.lerp as lerpFloat

private const val INITIAL_WANDER_PROGRESS = 0.5f
private const val WANDER_MIN_DURATION_MILLIS = 7000
private const val WANDER_MAX_DURATION_MILLIS = 12000
private const val GRADIENT_BLEND_DURATION_MILLIS = 750
private const val MONITORING_ACTIVE_BLEND = 1f
private const val MONITORING_INACTIVE_BLEND = 0f
private const val GRADIENT_CENTER_START = 0f
private const val GRADIENT_CENTER_END = 1f
private const val INACTIVE_RADIUS_WIDTH_FACTOR = 0.5f
private const val INACTIVE_RADIUS_SCALE = 1.08f
private const val ACTIVE_RADIUS_SCALE = 0.52f
private const val INACTIVE_INNER_FRACTION = 0.32f
private const val INACTIVE_MID_FRACTION = 0.62f
private const val ACTIVE_INNER_FRACTION = 0.2f
private const val ACTIVE_MID_FRACTION = 0.52f
private const val ACTIVE_OUTER_FRACTION = 0.11f

fun Modifier.appRootBackground(monitoringState: MonitoringState): Modifier =
    composed {
        val colorScheme = VartovyiTheme.colors
        val random = remember { Random(System.currentTimeMillis()) }
        val wanderXProgress = remember { Animatable(initialValue = INITIAL_WANDER_PROGRESS) }
        val wanderYProgress = remember { Animatable(initialValue = INITIAL_WANDER_PROGRESS) }

        LaunchedEffect(Unit) {
            while (true) {
                val targetXProgress = random.nextFloat()
                val targetYProgress = random.nextFloat()
                val durationMillis = random.nextInt(
                    from = WANDER_MIN_DURATION_MILLIS,
                    until = WANDER_MAX_DURATION_MILLIS,
                )

                coroutineScope {
                    launch {
                        wanderXProgress.animateTo(
                            targetValue = targetXProgress,
                            animationSpec = tween(
                                durationMillis = durationMillis,
                                easing = LinearEasing,
                            ),
                        )
                    }

                    launch {
                        wanderYProgress.animateTo(
                            targetValue = targetYProgress,
                            animationSpec = tween(
                                durationMillis = durationMillis,
                                easing = LinearEasing,
                            ),
                        )
                    }
                }
            }
        }

        val transition = updateTransition(
            targetState = monitoringState,
            label = "appRootBackground",
        )

        val blend by transition.animateFloat(
            transitionSpec = {
                tween(
                    durationMillis = GRADIENT_BLEND_DURATION_MILLIS,
                    easing = FastOutSlowInEasing,
                )
            },
            label = "gradientBlend",
        ) { state ->
            when (state) {
                MonitoringState.ACTIVE -> MONITORING_ACTIVE_BLEND
                MonitoringState.INACTIVE,
                MonitoringState.SCHEDULED,
                    -> MONITORING_INACTIVE_BLEND
            }
        }

        drawBehind {
            val background = colorScheme.background

            val inactiveCenter = Offset(
                x = size.width * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderXProgress.value,
                ),
                y = size.height * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderYProgress.value,
                ),
            )
            val inactiveRadius = hypot(
                x = size.width * INACTIVE_RADIUS_WIDTH_FACTOR,
                y = size.height,
            ) * INACTIVE_RADIUS_SCALE

            val inactiveInner = lerp(
                start = background,
                stop = colorScheme.tertiary,
                fraction = INACTIVE_INNER_FRACTION,
            )
            val inactiveMid = lerp(
                start = background,
                stop = colorScheme.surfaceVariant,
                fraction = INACTIVE_MID_FRACTION,
            )
            val inactiveOuter = background

            val activeCenter = Offset(
                x = size.width * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderXProgress.value,
                ),
                y = size.height * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderYProgress.value,
                ),
            )
            val activeRadius = min(
                a = size.width,
                b = size.height,
            ) * ACTIVE_RADIUS_SCALE

            val activeInner = lerp(
                start = background,
                stop = colorScheme.primary,
                fraction = ACTIVE_INNER_FRACTION,
            )
            val activeMid = lerp(
                start = background,
                stop = colorScheme.primaryContainer,
                fraction = ACTIVE_MID_FRACTION,
            )
            val activeOuter = lerp(
                start = background,
                stop = colorScheme.primary,
                fraction = ACTIVE_OUTER_FRACTION,
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
