package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.AlertEventStatus
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import kotlinx.coroutines.flow.flowOf

private const val LOG_PLACEHOLDER_KEY_PREFIX = "log_placeholder_"

@Composable
fun LogEventsList(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    logEntries: LazyPagingItems<AlertEvent>,
    onCopyChannelClick: (channelName: String) -> Unit,
    onCopyMessageClick: (messageText: String) -> Unit,
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier,
    ) {
        items(
            count = logEntries.itemCount,
            key = { index ->
                logEntries.peek(index)?.id ?: "$LOG_PLACEHOLDER_KEY_PREFIX$index"
            },
        ) { index ->
            val event = logEntries[index] ?: return@items

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
    val previewLogEntries = flowOf(
        PagingData.from(
            listOf(
                AlertEvent(
                    id = "1",
                    timestamp = 1_742_000_000_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "City News",
                    messageText = "Daily update without matching keyword",
                    matchedKeyword = "",
                    status = AlertEventStatus.SKIPPED,
                ),
                AlertEvent(
                    id = "2",
                    timestamp = 1_742_000_060_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Alert Channel",
                    messageText = "Attention, air alert reported! Attention, air rted! Attention, air alert reported!",
                    matchedKeyword = "air alert",
                    status = AlertEventStatus.ALARM_TRIGGERED,
                ),
                AlertEvent(
                    id = "3",
                    timestamp = 1_742_000_060_020,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Alert Channel",
                    messageText = "Attention, air alert reported! Attention, air rted! Attention, air alert reported!",
                    matchedKeyword = "air alert",
                    status = AlertEventStatus.SKIPPED_COOLDOWN,
                ),
            )
        )
    ).collectAsLazyPagingItems()

    VartovyiTheme {
        LogEventsList(
            listState = rememberLazyListState(),
            logEntries = previewLogEntries,
            onCopyChannelClick = {},
            onCopyMessageClick = {},
        )
    }
}
