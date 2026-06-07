package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.PopularChannelRegion
import com.revakovskyi.vartovyi.model.PopularTelegramChannel
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private const val SUGGESTIONS_MAX_HEIGHT_DP = 280
private const val BORDER_WIDTH_DP = 1
private const val CONTENT_TYPE_TITLE = "title"
private const val CONTENT_TYPE_HEADER = "header"
private const val CONTENT_TYPE_CHANNEL = "channel"
private const val TITLE_KEY = "popular_channels_title"
private const val HEADER_KEY_PREFIX = "header_"

@Composable
fun PopularChannelsSuggestions(
    modifier: Modifier = Modifier,
    visible: Boolean,
    channels: List<PopularTelegramChannel>,
    onSelect: (channel: String) -> Unit,
) {
    val channelsByRegion = remember(channels) { channels.groupBy { it.region } }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top),
        modifier = modifier,
    ) {
        Surface(
            color = VartovyiTheme.colors.surfaceVariant,
            shape = VartovyiTheme.shapes.medium,
            border = BorderStroke(
                width = BORDER_WIDTH_DP.dp,
                color = VartovyiTheme.colors.outline,
            ),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = SUGGESTIONS_MAX_HEIGHT_DP.dp)
            ) {
                item(
                    key = TITLE_KEY,
                    contentType = CONTENT_TYPE_TITLE
                ) {
                    Text(
                        text = stringResource(R.string.keywords_popular_channels_title),
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                        modifier = Modifier.padding(
                            horizontal = VartovyiTheme.spacing.standard,
                            vertical = VartovyiTheme.spacing.small,
                        )
                    )
                }

                PopularChannelRegion.entries.forEach { region ->
                    val regionChannels = channelsByRegion[region].orEmpty()
                    if (regionChannels.isEmpty()) return@forEach

                    item(
                        key = "$HEADER_KEY_PREFIX${region.name}",
                        contentType = CONTENT_TYPE_HEADER,
                    ) {
                        Text(
                            text = stringResource(region.titleResId()),
                            style = VartovyiTheme.typography.titleMedium,
                            color = VartovyiTheme.colors.primary,
                            modifier = Modifier.padding(
                                start = VartovyiTheme.spacing.standard,
                                end = VartovyiTheme.spacing.standard,
                                top = VartovyiTheme.spacing.medium,
                                bottom = VartovyiTheme.spacing.extraSmall,
                            )
                        )
                    }

                    items(
                        items = regionChannels,
                        key = { channel -> channel.handle },
                        contentType = { CONTENT_TYPE_CHANNEL },
                    ) { channel ->
                        SuggestedChannelRow(
                            channel = channel,
                            onSelect = onSelect,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedChannelRow(
    channel: PopularTelegramChannel,
    onSelect: (channel: String) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.micro),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(channel.displayName) }
            .padding(
                horizontal = VartovyiTheme.spacing.standard,
                vertical = VartovyiTheme.spacing.small,
            )
    ) {
        Text(
            text = channel.displayName,
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = channel.handle,
            style = VartovyiTheme.typography.bodySmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun PopularChannelRegion.titleResId(): Int = when (this) {
    PopularChannelRegion.NATIONAL -> R.string.keywords_popular_channels_group_national
    PopularChannelRegion.CHERNIHIV -> R.string.keywords_popular_channels_group_chernihiv
    PopularChannelRegion.SUMY -> R.string.keywords_popular_channels_group_sumy
    PopularChannelRegion.KYIV -> R.string.keywords_popular_channels_group_kyiv
    PopularChannelRegion.ZHYTOMYR -> R.string.keywords_popular_channels_group_zhytomyr
    PopularChannelRegion.KHARKIV -> R.string.keywords_popular_channels_group_kharkiv
    PopularChannelRegion.POLTAVA -> R.string.keywords_popular_channels_group_poltava
    PopularChannelRegion.DNIPRO -> R.string.keywords_popular_channels_group_dnipro
    PopularChannelRegion.ZAPORIZHZHIA -> R.string.keywords_popular_channels_group_zaporizhzhia
    PopularChannelRegion.MYKOLAIV -> R.string.keywords_popular_channels_group_mykolaiv
    PopularChannelRegion.KHERSON -> R.string.keywords_popular_channels_group_kherson
    PopularChannelRegion.ODESA -> R.string.keywords_popular_channels_group_odesa
}

@Preview(name = "Popular channels suggestions")
@Composable
private fun PreviewPopularChannelsSuggestions() {
    VartovyiTheme {
        PopularChannelsSuggestions(
            visible = true,
            channels = listOf(
                PopularTelegramChannel(
                    handle = "@kpszsu",
                    displayName = "Повітряні Сили ЗС України",
                    region = PopularChannelRegion.NATIONAL,
                ),
                PopularTelegramChannel(
                    handle = "@chernigiv_radar",
                    displayName = "Чернігів Радар",
                    region = PopularChannelRegion.CHERNIHIV,
                ),
                PopularTelegramChannel(
                    handle = "@monitor1654",
                    displayName = "monitor 1654 | Харків",
                    region = PopularChannelRegion.KHARKIV,
                ),
            ),
            onSelect = {},
        )
    }
}
