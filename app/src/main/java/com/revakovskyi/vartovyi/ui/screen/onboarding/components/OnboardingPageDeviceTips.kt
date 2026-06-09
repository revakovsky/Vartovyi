package com.revakovskyi.vartovyi.ui.screen.onboarding.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val COLLAPSED_CHEVRON_ROTATION_DEGREES = 0f
private const val EXPANDED_CHEVRON_ROTATION_DEGREES = 180f
private const val CHEVRON_ROTATION_DURATION_MILLIS = 250
private const val ENTER_FADE_DURATION_MILLIS = 220
private const val ENTER_EXPAND_DURATION_MILLIS = 260
private const val EXIT_FADE_DURATION_MILLIS = 120
private const val EXIT_SHRINK_DURATION_MILLIS = 220
private const val BULLET_PREFIX = "•"

private data class DeviceTipGroup(
    @param:StringRes val titleResId: Int,
    @param:StringRes val bodyResId: Int,
)

private val deviceTipGroups = listOf(
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_xiaomi_miui_title,
        bodyResId = R.string.onboarding_device_tips_xiaomi_miui_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_xiaomi_hyperos_title,
        bodyResId = R.string.onboarding_device_tips_xiaomi_hyperos_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_samsung_title,
        bodyResId = R.string.onboarding_device_tips_samsung_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_oppo_title,
        bodyResId = R.string.onboarding_device_tips_oppo_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_huawei_title,
        bodyResId = R.string.onboarding_device_tips_huawei_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_vivo_title,
        bodyResId = R.string.onboarding_device_tips_vivo_body,
    ),
    DeviceTipGroup(
        titleResId = R.string.onboarding_device_tips_other_title,
        bodyResId = R.string.onboarding_device_tips_other_body,
    ),
)

@Composable
fun OnboardingPageDeviceTips(
    modifier: Modifier = Modifier,
) {
    OnboardingPageLayout(
        visual = OnboardingVisual.VectorIcon(
            imageVector = ImageVector.vectorResource(R.drawable.devices),
            tint = VartovyiTheme.colors.primary,
        ),
        title = stringResource(R.string.onboarding_device_tips_title),
        bodyContent = { DeviceTipsBody() },
        modifier = modifier
    )
}

@Composable
private fun DeviceTipsBody() {
    val expandedGroupKeys = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.onboarding_device_tips_intro),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.large))

        Text(
            text = stringResource(R.string.onboarding_device_tips_lead),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.large))

        deviceTipGroups.forEachIndexed { index, group ->
            if (index > 0) {
                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))
            }

            DeviceTipExpandableItem(
                title = stringResource(group.titleResId),
                body = stringResource(group.bodyResId),
                isExpanded = expandedGroupKeys[group.titleResId] ?: false,
                onClick = {
                    val wasExpanded = expandedGroupKeys[group.titleResId] ?: false

                    expandedGroupKeys[group.titleResId] = !wasExpanded
                },
            )
        }
    }
}

@Composable
private fun DeviceTipExpandableItem(
    modifier: Modifier = Modifier,
    title: String,
    body: String,
    isExpanded: Boolean,
    onClick: () -> Unit,
) {
    val chevronRotationDegrees by animateFloatAsState(
        targetValue = if (isExpanded) {
            EXPANDED_CHEVRON_ROTATION_DEGREES
        } else {
            COLLAPSED_CHEVRON_ROTATION_DEGREES
        },
        animationSpec = tween(durationMillis = CHEVRON_ROTATION_DURATION_MILLIS),
        label = "deviceTipChevronRotation",
    )

    Surface(
        color = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(VartovyiTheme.spacing.standard)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                    )
            ) {
                Text(
                    text = title,
                    style = VartovyiTheme.typography.titleMedium,
                    color = VartovyiTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.small))

                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.down),
                    contentDescription = null,
                    tint = VartovyiTheme.colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(VartovyiTheme.spacing.large)
                        .graphicsLayer {
                            rotationZ = chevronRotationDegrees
                        },
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = ENTER_FADE_DURATION_MILLIS),
                ) + expandVertically(
                    animationSpec = tween(durationMillis = ENTER_EXPAND_DURATION_MILLIS),
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = EXIT_FADE_DURATION_MILLIS),
                ) + shrinkVertically(
                    animationSpec = tween(durationMillis = EXIT_SHRINK_DURATION_MILLIS),
                ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    HorizontalDivider(color = VartovyiTheme.colors.outline)

                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    DeviceTipBodyLines(body = body)
                }
            }
        }
    }
}

@Composable
private fun DeviceTipBodyLines(body: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        body.split("\n").forEach { line ->
            val isBullet = line.startsWith(BULLET_PREFIX)

            Text(
                text = line,
                style = if (isBullet) {
                    VartovyiTheme.typography.bodyMedium
                } else {
                    VartovyiTheme.typography.bodySmall
                },
                color = if (isBullet) {
                    VartovyiTheme.colors.onSurface
                } else {
                    VartovyiTheme.colors.onSurfaceVariant
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingPageDeviceTipsPreview() {
    VartovyiTheme {
        OnboardingPageDeviceTips(
            modifier = Modifier
                .fillMaxSize()
                .background(VartovyiTheme.colors.background)
        )
    }
}
