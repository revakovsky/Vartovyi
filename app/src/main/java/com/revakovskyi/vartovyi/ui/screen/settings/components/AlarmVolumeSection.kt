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

private const val MIN_ALARM_VOLUME_PERCENT = 0
private const val MAX_ALARM_VOLUME_PERCENT = 100
private const val ALARM_VOLUME_STEP_PERCENT = 5
private const val ALARM_VOLUME_SLIDER_WIDTH_FRACTION = 0.85f

@Composable
fun AlarmVolumeSection(
    modifier: Modifier = Modifier,
    volumePercent: Int,
    onVolumeChange: (percent: Int) -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(volumePercent.toFloat()) }
    val displayedVolumePercent = remember(sliderValue) { snapToAlarmVolumeStep(sliderValue) }

    LaunchedEffect(volumePercent) {
        sliderValue = volumePercent.toFloat()
    }

    VartovyiSettingSlider(
        title = stringResource(R.string.settings_alarm_volume_title),
        valueText = stringResource(
            R.string.settings_alarm_volume_value_percent,
            displayedVolumePercent,
        ),
        value = sliderValue,
        valueRange = MIN_ALARM_VOLUME_PERCENT.toFloat()..MAX_ALARM_VOLUME_PERCENT.toFloat(),
        steps = ((MAX_ALARM_VOLUME_PERCENT - MIN_ALARM_VOLUME_PERCENT) / ALARM_VOLUME_STEP_PERCENT) - 1,
        sliderWidthFraction = ALARM_VOLUME_SLIDER_WIDTH_FRACTION,
        onValueChange = { value ->
            val snappedVolumePercent = snapToAlarmVolumeStep(value)
            val snappedSliderValue = snappedVolumePercent.toFloat()

            if (sliderValue == snappedSliderValue) return@VartovyiSettingSlider

            sliderValue = snappedSliderValue
            onVolumeChange(snappedVolumePercent)
        },
        onValueChangeFinished = { onVolumeChange(displayedVolumePercent) },
        modifier = modifier
    )
}

private fun snapToAlarmVolumeStep(value: Float): Int {
    val steppedValue =
        (value / ALARM_VOLUME_STEP_PERCENT)
            .roundToInt() * ALARM_VOLUME_STEP_PERCENT

    return steppedValue.coerceIn(MIN_ALARM_VOLUME_PERCENT, MAX_ALARM_VOLUME_PERCENT)
}

@Preview
@Composable
private fun AlarmVolumeSectionPreview() {
    VartovyiTheme {
        AlarmVolumeSection(
            volumePercent = 70,
            onVolumeChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
