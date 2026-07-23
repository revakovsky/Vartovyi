package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.PopularTelegramChannel
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun TelegramChannelsSection(
    modifier: Modifier = Modifier,
    bringIntoViewRequester: BringIntoViewRequester,
    channels: List<String>,
    suggestedChannels: List<PopularTelegramChannel>,
    inputValue: String,
    onInputChange: (value: String) -> Unit,
    onAdd: () -> Unit,
    onCopy: (text: String) -> Unit,
    onRemove: (channel: String) -> Unit,
    onSuggestionSelect: (channel: String) -> Unit,
    onFocusChanged: (isFocused: Boolean) -> Unit,
) {
    var isInputFocused by remember { mutableStateOf(false) }

    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        ) {
            SectionTitle(
                title = stringResource(R.string.keywords_telegram_channels),
                tooltipText = stringResource(R.string.keywords_telegram_channel_tooltip),
            )

            Text(
                text = stringResource(
                    if (channels.isEmpty()) R.string.keywords_telegram_channels_empty_hint
                    else R.string.keywords_telegram_channel_tip
                ),
                style = VartovyiTheme.typography.bodySmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
            ) {
                WordInputRow(
                    value = inputValue,
                    hint = stringResource(R.string.keywords_telegram_channel_hint),
                    onClear = { onInputChange("") },
                    onValueChange = onInputChange,
                    onAdd = onAdd,
                    onFocusChanged = { isFocused ->
                        isInputFocused = isFocused
                        onFocusChanged(isFocused)
                    },
                    modifier = Modifier.bringIntoViewRequester(bringIntoViewRequester)
                )

                PopularChannelsSuggestions(
                    visible = isInputFocused && suggestedChannels.isNotEmpty(),
                    channels = suggestedChannels,
                    onSelect = onSuggestionSelect,
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
                                onLongPress = { onCopy(channel) },
                                onRemove = { onRemove(channel) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Telegram channels section — empty")
@Composable
private fun PreviewTelegramChannelsSectionEmpty() {
    VartovyiTheme {
        TelegramChannelsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            channels = emptyList(),
            suggestedChannels = emptyList(),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onCopy = {},
            onRemove = {},
            onSuggestionSelect = {},
            onFocusChanged = {},
        )
    }
}

@Preview(name = "Telegram channels section — with channels")
@Composable
private fun PreviewTelegramChannelsSectionWithChannels() {
    VartovyiTheme {
        TelegramChannelsSection(
            bringIntoViewRequester = remember { BringIntoViewRequester() },
            channels = listOf("@air_alert_ua", "@kharkiv_alarm"),
            suggestedChannels = emptyList(),
            inputValue = "",
            onInputChange = {},
            onAdd = {},
            onCopy = {},
            onRemove = {},
            onSuggestionSelect = {},
            onFocusChanged = {},
        )
    }
}
