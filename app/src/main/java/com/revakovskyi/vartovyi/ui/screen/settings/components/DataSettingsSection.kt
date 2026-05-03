package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.screen.keywords.components.SectionTitle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private val LOG_SIZE_OPTIONS = listOf(100, 300, 500, 1000)
private val COOLDOWN_DURATION_OPTIONS_MINUTES = listOf(1, 3, 5, 10)
private const val MILLIS_IN_MINUTE = 60_000L

@Composable
fun DataSettingsSection(
    modifier: Modifier = Modifier,
    currentLogSizeLimit: Int,
    currentAlarmCooldownDurationMillis: Long,
    onLogSizeLimitChange: (limit: Int) -> Unit,
    onAlarmCooldownDurationChange: (durationMillis: Long) -> Unit,
    onResetToFactoryDefaultsClick: () -> Unit,
) {
    val sortedLogSizeOptions = remember { LOG_SIZE_OPTIONS.sorted() }
    val sortedCooldownDurationOptionsMinutes = remember {
        COOLDOWN_DURATION_OPTIONS_MINUTES.sorted()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        SectionTitle(
            title = stringResource(R.string.settings_data_log_size_title),
            tooltipText = stringResource(R.string.settings_data_log_size_tooltip),
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

        Row(
            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            sortedLogSizeOptions.forEach { logSizeLimit ->
                FilterChip(
                    selected = currentLogSizeLimit == logSizeLimit,
                    onClick = { onLogSizeLimitChange(logSizeLimit) },
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = currentLogSizeLimit == logSizeLimit,
                        borderColor = VartovyiTheme.colors.onSurfaceVariant,
                        selectedBorderColor = VartovyiTheme.colors.secondary,
                        disabledBorderColor = VartovyiTheme.colors.outline,
                        disabledSelectedBorderColor = VartovyiTheme.colors.outline,
                    ),
                    label = {
                        Text(
                            text = stringResource(
                                R.string.settings_data_log_size_value_items,
                                logSizeLimit
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        SectionTitle(
            title = stringResource(R.string.settings_data_alarm_cooldown_title),
            tooltipText = stringResource(R.string.settings_data_alarm_cooldown_tooltip)
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

        Row(
            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            sortedCooldownDurationOptionsMinutes.forEach { cooldownDurationMinutes ->
                val cooldownDurationMillis = cooldownDurationMinutes * MILLIS_IN_MINUTE
                FilterChip(
                    selected = currentAlarmCooldownDurationMillis == cooldownDurationMillis,
                    onClick = {
                        onAlarmCooldownDurationChange(cooldownDurationMillis)
                    },
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = currentAlarmCooldownDurationMillis == cooldownDurationMillis,
                        borderColor = VartovyiTheme.colors.onSurfaceVariant,
                        selectedBorderColor = VartovyiTheme.colors.secondary,
                        disabledBorderColor = VartovyiTheme.colors.outline,
                        disabledSelectedBorderColor = VartovyiTheme.colors.outline,
                    ),
                    label = {
                        Text(
                            text = stringResource(
                                R.string.settings_data_alarm_cooldown_value_minutes,
                                cooldownDurationMinutes,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.large))

        SettingsResetFactoryDefaultsButton(
            onClick = onResetToFactoryDefaultsClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun DataSettingsSectionPreview() {
    VartovyiTheme {
        DataSettingsSection(
            currentLogSizeLimit = 200,
            currentAlarmCooldownDurationMillis = 5 * MILLIS_IN_MINUTE,
            onLogSizeLimitChange = {},
            onAlarmCooldownDurationChange = {},
            onResetToFactoryDefaultsClick = {},
        )
    }
}
