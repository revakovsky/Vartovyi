package com.revakovskyi.vartovyi.ui.screen.settings.components

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiSwitch
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import java.util.Locale

private const val DEFAULT_START_HOUR = 22
private const val DEFAULT_START_MINUTE = 0
private const val DEFAULT_END_HOUR = 7
private const val DEFAULT_END_MINUTE = 0
private const val TIME_SEPARATOR = ":"
private const val TIME_PARTS_SIZE = 2
private const val MAX_HOUR = 23
private const val MAX_MINUTE = 59

@Composable
fun ScheduleSettingsSection(
    modifier: Modifier = Modifier,
    isScheduleEnabled: Boolean,
    startTime: String,
    endTime: String,
    onScheduleEnabledChange: (enabled: Boolean) -> Unit,
    onStartTimeChange: (time: String) -> Unit,
    onEndTimeChange: (time: String) -> Unit,
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_schedule_enabled_title),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )

            VartovyiSwitch(
                checked = isScheduleEnabled,
                onCheckedChange = onScheduleEnabledChange,
            )
        }

        if (isScheduleEnabled) {
            Text(
                text = stringResource(
                    R.string.settings_schedule_hint_active_window,
                    startTime,
                    endTime,
                ),
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )

            TimeSettingRow(
                title = stringResource(R.string.settings_schedule_start_title),
                selectedTime = startTime,
                onTimeSelected = { selectedTime ->
                    onStartTimeChange(selectedTime)
                },
                onPickTime = { onTimeSelected ->
                    showTimePickerDialog(
                        context = context,
                        initialTime = startTime,
                        defaultHour = DEFAULT_START_HOUR,
                        defaultMinute = DEFAULT_START_MINUTE,
                        onTimeSelected = onTimeSelected,
                    )
                },
            )

            TimeSettingRow(
                title = stringResource(R.string.settings_schedule_end_title),
                selectedTime = endTime,
                onTimeSelected = { selectedTime ->
                    onEndTimeChange(selectedTime)
                },
                onPickTime = { onTimeSelected ->
                    showTimePickerDialog(
                        context = context,
                        initialTime = endTime,
                        defaultHour = DEFAULT_END_HOUR,
                        defaultMinute = DEFAULT_END_MINUTE,
                        onTimeSelected = onTimeSelected,
                    )
                },
            )
        } else {
            Text(
                text = stringResource(R.string.settings_schedule_hint_always_on),
                style = VartovyiTheme.typography.bodyMedium,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TimeSettingRow(
    modifier: Modifier = Modifier,
    title: String,
    selectedTime: String,
    onTimeSelected: (time: String) -> Unit,
    onPickTime: (onTimeSelected: (time: String) -> Unit) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )

        OutlinedButton(
            onClick = { onPickTime(onTimeSelected) },
            border = BorderStroke(
                width = 1.dp,
                color = VartovyiTheme.colors.primary.copy(alpha = 0.75f),
            ),
            modifier = Modifier.height(40.dp)
        ) {
            Text(
                text = selectedTime,
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
            )
        }
    }
}

private fun showTimePickerDialog(
    context: Context,
    initialTime: String,
    defaultHour: Int,
    defaultMinute: Int,
    onTimeSelected: (time: String) -> Unit,
) {
    val (initialHour, initialMinute) = parseTimeOrDefault(
        value = initialTime,
        defaultHour = defaultHour,
        defaultMinute = defaultMinute,
    )

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            onTimeSelected(
                String.format(
                    Locale.ROOT,
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute,
                ),
            )
        },
        initialHour,
        initialMinute,
        true,
    ).show()
}

private fun parseTimeOrDefault(
    value: String,
    defaultHour: Int,
    defaultMinute: Int,
): Pair<Int, Int> {
    val parts = value.split(TIME_SEPARATOR)
    if (parts.size != TIME_PARTS_SIZE) return defaultHour to defaultMinute

    val parsedHour = parts.first().toIntOrNull()
    val parsedMinute = parts.last().toIntOrNull()

    val hour = parsedHour?.takeIf { it in 0..MAX_HOUR } ?: defaultHour
    val minute = parsedMinute?.takeIf { it in 0..MAX_MINUTE } ?: defaultMinute

    return hour to minute
}

@Preview
@Composable
private fun ScheduleSettingsSectionPreviewEnabled() {
    VartovyiTheme {
        ScheduleSettingsSection(
            isScheduleEnabled = true,
            startTime = "22:00",
            endTime = "07:00",
            onScheduleEnabledChange = {},
            onStartTimeChange = {},
            onEndTimeChange = {},
        )
    }
}

@Preview
@Composable
private fun ScheduleSettingsSectionPreviewDisabled() {
    VartovyiTheme {
        ScheduleSettingsSection(
            isScheduleEnabled = false,
            startTime = "22:00",
            endTime = "07:00",
            onScheduleEnabledChange = {},
            onStartTimeChange = {},
            onEndTimeChange = {},
        )
    }
}
