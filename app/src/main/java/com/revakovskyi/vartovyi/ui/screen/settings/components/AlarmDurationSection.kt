package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import kotlin.math.roundToInt

private const val MIN_ALARM_DURATION_SECONDS = 5
private const val MAX_ALARM_DURATION_SECONDS = 300
private const val ALARM_DURATION_STEP_SECONDS = 30
private const val ALARM_DURATION_SLIDER_WIDTH_FRACTION = 0.85f

@Composable
fun AlarmDurationSection(
    modifier: Modifier = Modifier,
    durationSeconds: Int,
    onDurationChange: (seconds: Int) -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(durationSeconds.toFloat()) }
    val displayedDurationSeconds = remember(sliderValue) { snapToAlarmDurationStep(sliderValue) }

    LaunchedEffect(durationSeconds) {
        sliderValue = durationSeconds.toFloat()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_alarm_duration_title),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onBackground,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(
                    R.string.settings_alarm_duration_value_seconds,
                    displayedDurationSeconds,
                ),
                style = VartovyiTheme.typography.bodyLarge,
                color = VartovyiTheme.colors.secondary,
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = { value ->
                val snappedDurationSeconds = snapToAlarmDurationStep(value)
                val snappedSliderValue = snappedDurationSeconds.toFloat()

                if (sliderValue == snappedSliderValue) return@Slider

                sliderValue = snappedSliderValue
                onDurationChange(snappedDurationSeconds)
            },
            onValueChangeFinished = {
                onDurationChange(displayedDurationSeconds)
            },
            valueRange = MIN_ALARM_DURATION_SECONDS.toFloat()..MAX_ALARM_DURATION_SECONDS.toFloat(),
            steps = ((MAX_ALARM_DURATION_SECONDS - MIN_ALARM_DURATION_SECONDS) / ALARM_DURATION_STEP_SECONDS) - 1,
            colors = SliderDefaults.colors(
                thumbColor = VartovyiTheme.colors.secondary,
                activeTrackColor = VartovyiTheme.colors.primary,
                inactiveTrackColor = VartovyiTheme.colors.onSurfaceVariant,
                activeTickColor = VartovyiTheme.colors.primaryContainer,
                inactiveTickColor = VartovyiTheme.colors.outline,
            ),
            modifier = Modifier.fillMaxWidth(ALARM_DURATION_SLIDER_WIDTH_FRACTION)
        )
    }
}

private fun snapToAlarmDurationStep(value: Float): Int {
    val steppedValue =
        (value / ALARM_DURATION_STEP_SECONDS)
            .roundToInt() * ALARM_DURATION_STEP_SECONDS

    return steppedValue.coerceIn(MIN_ALARM_DURATION_SECONDS, MAX_ALARM_DURATION_SECONDS)
}

@Preview
@Composable
private fun AlarmDurationSectionPreview() {
    VartovyiTheme {
        AlarmDurationSection(
            durationSeconds = 60,
            onDurationChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
