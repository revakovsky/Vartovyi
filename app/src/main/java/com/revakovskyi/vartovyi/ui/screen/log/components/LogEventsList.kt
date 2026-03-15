package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun LogEventsList(
    modifier: Modifier = Modifier,
    logEntries: List<AlertEvent>,
    onCopyChannelClick: (channelName: String) -> Unit,
    onCopyMessageClick: (messageText: String) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier,
    ) {
        items(
            items = logEntries,
            key = { event -> event.id },
        ) { event ->
            LogEventItemCard(
                event = event,
                onCopyChannelClick = onCopyChannelClick,
                onCopyMessageClick = onCopyMessageClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(name = "Log events list")
@Composable
private fun LogEventsListPreview() {
    VartovyiTheme {
        LogEventsList(
            logEntries = listOf(
                AlertEvent(
                    id = "1",
                    timestamp = 1_742_000_000_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "City News",
                    messageText = "Daily update without matching keyword",
                    matchedKeyword = "",
                ),
                AlertEvent(
                    id = "2",
                    timestamp = 1_742_000_060_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Alert Channel",
                    messageText = "Attention, air alert reported! Attention, air rted! Attention, air alert reported!",
                    matchedKeyword = "air alert",
                ),
            ),
            onCopyChannelClick = {},
            onCopyMessageClick = {},
        )
    }
}
