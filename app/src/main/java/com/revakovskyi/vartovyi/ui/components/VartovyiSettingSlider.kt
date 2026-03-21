package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
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
    isStepHapticFeedbackEnabled: Boolean = true,
    discreteValueForHaptics: (rawValue: Float) -> Float = { rawValue -> rawValue },
    onValueChange: (value: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onBackground,
                modifier = Modifier.weight(1f)
            )

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
}

@Preview
@Composable
private fun VartovyiSettingSliderPreview() {
    VartovyiTheme {
        VartovyiSettingSlider(
            title = "Alarm duration",
            valueText = "60 s",
            value = 60f,
            valueRange = 5f..300f,
            steps = 9,
            sliderWidthFraction = 0.85f,
            onValueChange = {},
            onValueChangeFinished = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
