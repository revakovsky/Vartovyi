package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiSwitch
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun TelegramChannelsSection(
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester,
    isEnabled: Boolean,
    channels: List<String>,
    inputValue: String,
    onToggle: () -> Unit,
    onInputChange: (value: String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (channel: String) -> Unit,
    onFocusChanged: (isFocused: Boolean) -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                ) {
                    Text(
                        text = stringResource(R.string.keywords_telegram_channels),
                        style = VartovyiTheme.typography.titleMedium,
                        color = VartovyiTheme.colors.onSurface,
                    )

                    Text(
                        text = stringResource(R.string.keywords_optional),
                        style = VartovyiTheme.typography.bodySmall,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                    )
                }

                VartovyiSwitch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() }
                )
            }

            if (isEnabled) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
                    modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester),
                ) {
                    WordInputRow(
                        value = inputValue,
                        hint = stringResource(R.string.keywords_telegram_channel_hint),
                        onValueChange = onInputChange,
                        onAdd = onAdd,
                        onFocusChanged = onFocusChanged,
                    )

                    if (channels.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                        ) {
                            channels.forEach { channel ->
                                WordChip(
                                    text = channel,
                                    containerColor = VartovyiTheme.colors.tertiaryContainer,
                                    contentColor = VartovyiTheme.colors.onTertiaryContainer,
                                    onRemove = { onRemove(channel) },
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.keywords_telegram_channel_tip),
                    style = VartovyiTheme.typography.bodySmall,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            } else {
                Text(
                    text = stringResource(R.string.keywords_all_channels_monitored),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview(name = "Telegram channels section — disabled")
@Composable
private fun PreviewTelegramChannelsSectionDisabled() {
    VartovyiTheme {
        TelegramChannelsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            isEnabled = false,
            channels = emptyList(),
            inputValue = "",
            onToggle = {},
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}

@Preview(name = "Telegram channels section — enabled, empty")
@Composable
private fun PreviewTelegramChannelsSectionEnabledEmpty() {
    VartovyiTheme {
        TelegramChannelsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            isEnabled = true,
            channels = emptyList(),
            inputValue = "",
            onToggle = {},
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}

@Preview(name = "Telegram channels section — enabled, with channels")
@Composable
private fun PreviewTelegramChannelsSectionWithChannels() {
    VartovyiTheme {
        TelegramChannelsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            isEnabled = true,
            channels = listOf("@air_alert_ua", "@kharkiv_alarm"),
            inputValue = "",
            onToggle = {},
            onInputChange = {},
            onAdd = {},
            onRemove = {},
            onFocusChanged = {},
        )
    }
}
