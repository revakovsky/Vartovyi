package com.revakovskyi.vartovyi.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import com.revakovskyi.vartovyi.model.MonitoringState
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import androidx.compose.ui.util.lerp as lerpFloat

private const val WANDER_PROGRESS_MIN = 0f
private const val WANDER_PROGRESS_MAX = 1f
private const val WANDER_X_DURATION_MILLIS = 9000
private const val WANDER_Y_DURATION_MILLIS = 11000
private const val DRIFT_PROGRESS_MIN = 0f
private const val DRIFT_PROGRESS_MAX = 1f
private const val DRIFT_AMPLITUDE_MIN = 0.08f
private const val DRIFT_AMPLITUDE_MAX = 0.2f
private const val DRIFT_DURATION_MIN_MILLIS = 5000
private const val DRIFT_DURATION_MAX_MILLIS = 13000
private const val HALF_CYCLE_RADIANS = Math.PI.toFloat()
private const val FULL_CYCLE_RADIANS = (Math.PI * 2).toFloat()
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

@Suppress("LongMethod")
fun Modifier.appRootBackground(monitoringState: MonitoringState): Modifier =
    composed {
        val colorScheme = VartovyiTheme.colors
        val random = remember { Random.Default }
        val driftAmplitudeX = remember {
            random.nextFloat() * (DRIFT_AMPLITUDE_MAX - DRIFT_AMPLITUDE_MIN) + DRIFT_AMPLITUDE_MIN
        }
        val driftAmplitudeY = remember {
            random.nextFloat() * (DRIFT_AMPLITUDE_MAX - DRIFT_AMPLITUDE_MIN) + DRIFT_AMPLITUDE_MIN
        }
        val driftPhaseX = remember { random.nextFloat() * FULL_CYCLE_RADIANS }
        val driftPhaseY = remember { random.nextFloat() * FULL_CYCLE_RADIANS }

        val driftDurationXMillis = remember {
            random.nextInt(
                from = DRIFT_DURATION_MIN_MILLIS,
                until = DRIFT_DURATION_MAX_MILLIS,
            )
        }

        val driftDurationYMillis = remember {
            random.nextInt(
                from = DRIFT_DURATION_MIN_MILLIS,
                until = DRIFT_DURATION_MAX_MILLIS,
            )
        }

        val wanderTransition = rememberInfiniteTransition(label = "appRootWanderTransition")

        val baseWanderXProgress by wanderTransition.animateFloat(
            initialValue = WANDER_PROGRESS_MIN,
            targetValue = WANDER_PROGRESS_MAX,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = WANDER_X_DURATION_MILLIS,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "appRootBaseWanderXProgress",
        )

        val baseWanderYProgress by wanderTransition.animateFloat(
            initialValue = WANDER_PROGRESS_MAX,
            targetValue = WANDER_PROGRESS_MIN,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = WANDER_Y_DURATION_MILLIS,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "appRootBaseWanderYProgress",
        )

        val driftCycleXProgress by wanderTransition.animateFloat(
            initialValue = DRIFT_PROGRESS_MIN,
            targetValue = DRIFT_PROGRESS_MAX,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = driftDurationXMillis,
                    easing = LinearEasing,
                )
            ),
            label = "appRootDriftCycleXProgress",
        )

        val driftCycleYProgress by wanderTransition.animateFloat(
            initialValue = DRIFT_PROGRESS_MIN,
            targetValue = DRIFT_PROGRESS_MAX,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = driftDurationYMillis,
                    easing = LinearEasing,
                )
            ),
            label = "appRootDriftCycleYProgress",
        )

        val driftOffsetX = sin(
            x = driftCycleXProgress * FULL_CYCLE_RADIANS + driftPhaseX
        ) * driftAmplitudeX

        val driftOffsetY = sin(
            x = driftCycleYProgress * FULL_CYCLE_RADIANS + driftPhaseY + HALF_CYCLE_RADIANS
        ) * driftAmplitudeY

        val wanderXProgress = (baseWanderXProgress + driftOffsetX).coerceIn(
            minimumValue = WANDER_PROGRESS_MIN,
            maximumValue = WANDER_PROGRESS_MAX,
        )

        val wanderYProgress = (baseWanderYProgress + driftOffsetY).coerceIn(
            minimumValue = WANDER_PROGRESS_MIN,
            maximumValue = WANDER_PROGRESS_MAX,
        )

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
                    fraction = wanderXProgress,
                ),
                y = size.height * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderYProgress,
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

            val activeCenter = Offset(
                x = size.width * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderXProgress,
                ),
                y = size.height * lerpFloat(
                    start = GRADIENT_CENTER_START,
                    stop = GRADIENT_CENTER_END,
                    fraction = wanderYProgress,
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
                start = background,
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
