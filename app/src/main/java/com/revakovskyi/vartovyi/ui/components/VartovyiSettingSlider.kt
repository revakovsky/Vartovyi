package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiSettingSlider(
    modifier: Modifier = Modifier,
    title: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    sliderWidthFraction: Float,
    tooltipText: String? = null,
    isStepHapticFeedbackEnabled: Boolean = true,
    discreteValueForHaptics: (rawValue: Float) -> Float = { rawValue -> rawValue },
    onValueChange: (value: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    var isTooltipDialogVisible by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = VartovyiTheme.typography.titleMedium,
                    color = VartovyiTheme.colors.onBackground,
                )

                if (tooltipText != null) {
                    FilledTonalIconButton(
                        onClick = { isTooltipDialogVisible = true },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(alpha = 0.35f),
                        ),
                        modifier = Modifier.size(VartovyiTheme.spacing.extraLarge),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.info),
                            contentDescription = null,
                            modifier = Modifier.size(VartovyiTheme.spacing.standard),
                        )
                    }
                }
            }

            Text(
                text = valueText,
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.secondary,
            )
        }

        Slider(
            value = value,
            onValueChange = { newValue ->
                if (isStepHapticFeedbackEnabled) {
                    val discreteNew = discreteValueForHaptics(newValue)

                    if (discreteNew != value) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    }
                }

                onValueChange(newValue)
            },
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = VartovyiTheme.colors.onTertiaryContainer,
                activeTrackColor = VartovyiTheme.colors.primary,
                inactiveTrackColor = VartovyiTheme.colors.onSurfaceVariant,
                activeTickColor = VartovyiTheme.colors.primaryContainer,
                inactiveTickColor = VartovyiTheme.colors.outline,
            ),
            modifier = Modifier.fillMaxWidth(sliderWidthFraction)
        )
    }

    if (isTooltipDialogVisible && tooltipText != null) {
        VartovyiDialog(
            title = title,
            message = tooltipText,
            confirmText = stringResource(R.string.ok),
            onDismiss = { isTooltipDialogVisible = false },
        )
    }
}

@Preview(name = "Setting slider - with tooltip")
@Composable
private fun VartovyiSettingSliderWithTooltipPreview() {
    VartovyiTheme {
        VartovyiSettingSlider(
            title = "Alarm duration",
            valueText = "60 s",
            value = 60f,
            valueRange = 5f..300f,
            steps = 9,
            sliderWidthFraction = 0.85f,
            tooltipText = "How long the alarm keeps ringing.",
            onValueChange = {},
            onValueChangeFinished = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Setting slider - without tooltip")
@Composable
private fun VartovyiSettingSliderWithoutTooltipPreview() {
    VartovyiTheme {
        VartovyiSettingSlider(
            title = "Alarm volume",
            valueText = "70%",
            value = 70f,
            valueRange = 0f..100f,
            steps = 19,
            sliderWidthFraction = 0.85f,
            tooltipText = null,
            onValueChange = {},
            onValueChangeFinished = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
