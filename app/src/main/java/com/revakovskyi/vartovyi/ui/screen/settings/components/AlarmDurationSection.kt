package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiSettingSlider
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

    VartovyiSettingSlider(
        title = stringResource(R.string.settings_alarm_duration_title),
        valueText = stringResource(
            R.string.settings_alarm_duration_value_seconds,
            displayedDurationSeconds,
        ),
        value = sliderValue,
        valueRange = MIN_ALARM_DURATION_SECONDS.toFloat()..MAX_ALARM_DURATION_SECONDS.toFloat(),
        steps = ((MAX_ALARM_DURATION_SECONDS - MIN_ALARM_DURATION_SECONDS) / ALARM_DURATION_STEP_SECONDS) - 1,
        sliderWidthFraction = ALARM_DURATION_SLIDER_WIDTH_FRACTION,
        onValueChange = { value ->
            val snappedDurationSeconds = snapToAlarmDurationStep(value)
            val snappedSliderValue = snappedDurationSeconds.toFloat()

            if (sliderValue == snappedSliderValue) {
                return@VartovyiSettingSlider
            }

            sliderValue = snappedSliderValue
            onDurationChange(snappedDurationSeconds)
        },
        onValueChangeFinished = {
            onDurationChange(displayedDurationSeconds)
        },
        modifier = modifier
    )
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
