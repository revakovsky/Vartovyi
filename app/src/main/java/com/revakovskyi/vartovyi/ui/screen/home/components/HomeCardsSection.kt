package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun HomeCardsSection(
    modifier: Modifier = Modifier,
    state: HomeUiContract.State,
    onAction: (action: HomeUiContract.Action) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier.padding(horizontal = VartovyiTheme.spacing.small)
    ) {
        if (state.needsKeywordsAttention) {
            KeywordsCard(
                keywords = state.keywords,
                onClick = { onAction(HomeUiContract.Action.NavigateToKeywords) },
            )
        }

        LastAlertCard(
            lastAlertEvent = state.lastAlertEvent,
            onClick = { onAction(HomeUiContract.Action.NavigateToLog()) },
            onEventClick = {
                onAction(
                    HomeUiContract.Action.NavigateToLog(
                        logEntryId = state.lastAlertEvent?.id,
                    ),
                )
            },
            modifier = Modifier.padding(bottom = VartovyiTheme.spacing.small)
        )
    }
}

@Preview(name = "Home cards — needs attention")
@Composable
private fun HomeCardsSectionPreview() {
    VartovyiTheme {
        HomeCardsSection(
            state = HomeUiContract.State(
                isLoading = false,
                keywords = listOf("<НАЗВА_МІСТА>"),
                lastAlertEvent = AlertEvent(
                    id = "1",
                    timestamp = 1_700_000_000_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Тривога • Харків",
                    messageText = "Увага! Повітряна тривога в області.",
                    matchedKeyword = "харків",
                    status = AlertEventStatus.ALARM_TRIGGERED,
                ),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Home cards — no triggers yet")
@Composable
private fun HomeCardsSectionEmptyPreview() {
    VartovyiTheme {
        HomeCardsSection(
            state = HomeUiContract.State(isLoading = false),
            onAction = {},
        )
    }
}
