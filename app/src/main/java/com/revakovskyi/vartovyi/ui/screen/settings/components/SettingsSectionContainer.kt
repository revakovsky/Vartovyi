package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val SECTION_ENTER_FADE_DURATION_MILLIS = 220
private const val SECTION_ENTER_EXPAND_DURATION_MILLIS = 260
private const val SECTION_EXIT_FADE_DURATION_MILLIS = 120
private const val SECTION_EXIT_SHRINK_DURATION_MILLIS = 220
private const val EXPAND_ICON_ROTATION_DURATION_MILLIS = 250
private const val EXPAND_ICON_COLLAPSED_ROTATION_DEGREES = 0f
private const val EXPAND_ICON_EXPANDED_ROTATION_DEGREES = 180f
private const val INFO_ICON_BACKGROUND_ALPHA = 0.35f

@Composable
fun SettingsSectionContainer(
    modifier: Modifier = Modifier,
    title: String,
    titleTooltipText: String? = null,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surfaceVariant,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(VartovyiTheme.spacing.standard)
        ) {
            SettingsSectionTitleRow(
                title = title,
                tooltipText = titleTooltipText,
                isExpanded = isExpanded,
                onHeaderClick = onHeaderClick,
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = SECTION_ENTER_FADE_DURATION_MILLIS),
                ) + expandVertically(
                    animationSpec = tween(durationMillis = SECTION_ENTER_EXPAND_DURATION_MILLIS),
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = SECTION_EXIT_FADE_DURATION_MILLIS),
                ) + shrinkVertically(
                    animationSpec = tween(durationMillis = SECTION_EXIT_SHRINK_DURATION_MILLIS),
                ),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    HorizontalDivider(color = VartovyiTheme.colors.outline)

                    Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

                    content()
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionTitleRow(
    title: String,
    tooltipText: String?,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val expandIconRotationDegrees by animateFloatAsState(
        targetValue = if (isExpanded) {
            EXPAND_ICON_EXPANDED_ROTATION_DEGREES
        } else {
            EXPAND_ICON_COLLAPSED_ROTATION_DEGREES
        },
        animationSpec = tween(durationMillis = EXPAND_ICON_ROTATION_DURATION_MILLIS),
        label = "settingsSectionExpandIconRotation",
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onHeaderClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            modifier = Modifier
                .weight(1f)
                .defaultMinSize(minHeight = VartovyiTheme.spacing.extraLarge)
                .padding(vertical = VartovyiTheme.spacing.extraSmall)
        ) {
            Text(
                text = title,
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
                modifier = Modifier.weight(1f),
            )

            if (tooltipText != null) {
                FilledTonalIconButton(
                    onClick = { showDialog = true },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(
                            alpha = INFO_ICON_BACKGROUND_ALPHA,
                        ),
                    ),
                    shape = CircleShape,
                    modifier = Modifier.size(VartovyiTheme.spacing.extraLarge)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.info),
                        contentDescription = null,
                        modifier = Modifier.size(VartovyiTheme.spacing.standard)
                    )
                }

                Spacer(modifier = Modifier.width(VartovyiTheme.spacing.small))
            }

            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.down),
                contentDescription = null,
                tint = VartovyiTheme.colors.onSurfaceVariant,
                modifier = Modifier
                    .size(VartovyiTheme.spacing.large)
                    .graphicsLayer {
                        rotationZ = expandIconRotationDegrees
                    },
            )
        }
    }

    if (showDialog && tooltipText != null) {
        VartovyiDialog(
            title = title,
            message = tooltipText,
            confirmText = stringResource(R.string.ok),
            onDismiss = { showDialog = false },
        )
    }
}

@Preview(name = "Title only")
@Composable
private fun SettingsSectionContainerPreviewTitleOnly() {
    VartovyiTheme {
        SettingsSectionContainer(
            title = "Data",
            isExpanded = true,
            onHeaderClick = {},
        ) {
            Text(
                text = "Section body",
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "With info button")
@Composable
private fun SettingsSectionContainerPreviewWithTooltip() {
    VartovyiTheme {
        SettingsSectionContainer(
            title = "Work schedule",
            titleTooltipText = "When enabled, keyword checks apply only inside the selected time range.",
            isExpanded = true,
            onHeaderClick = {},
        ) {
            Text(
                text = "Toggle and time rows would go here.",
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}
