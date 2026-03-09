package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.ui.screen.home.components.KeywordsCard
import com.revakovskyi.vartovyi.ui.screen.home.components.LastAlertCard
import com.revakovskyi.vartovyi.ui.screen.home.components.StatusBlock
import com.revakovskyi.vartovyi.ui.screen.home.components.TestAlarmButton
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToKeywords: () -> Unit,
    onNavigateToLog: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPermissions: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is HomeUiContract.Event.NavigateToKeywords -> onNavigateToKeywords()
            is HomeUiContract.Event.NavigateToLog -> onNavigateToLog()
            is HomeUiContract.Event.NavigateToSettings -> onNavigateToSettings()
            is HomeUiContract.Event.NavigateToPermissions -> onNavigateToPermissions()
            else -> Unit
        }
    }

    HomeContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun HomeContent(
    state: HomeUiContract.State,
    onAction: (action: HomeUiContract.Action) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val statusBlockHeight = maxHeight / 2

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            StatusBlock(
                monitoringState = state.monitoringState,
                onToggle = { onAction(HomeUiContract.Action.ToggleMonitoring) },
                modifier = Modifier.height(statusBlockHeight),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
                modifier = Modifier.padding(VartovyiTheme.spacing.standard),
            ) {
                KeywordsCard(
                    keywords = state.keywords,
                    onAddKeywords = { onAction(HomeUiContract.Action.NavigateToKeywords) },
                )

                LastAlertCard(lastAlertEvent = state.lastAlertEvent)

                TestAlarmButton(onClick = { onAction(HomeUiContract.Action.TestAlarm) })
            }
        }
    }
}

@Preview(
    name = "Inactive — empty",
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun HomeContentInactivePreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(),
            onAction = {},
        )
    }
}

@Preview(
    name = "Active — with keywords",
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun HomeContentActiveWithKeywordsPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(
                monitoringState = MonitoringState.ACTIVE,
                keywords = listOf("ракета", "вибух", "тривога", "атака", "бомба"),
            ),
            onAction = {},
        )
    }
}

@Preview(
    name = "Active — with last alert",
    widthDp = 360,
    heightDp = 800,
)
@Composable
private fun HomeContentActiveWithAlertPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(
                monitoringState = MonitoringState.ACTIVE,
                keywords = listOf("ракета", "тривога"),
                lastAlertEvent = AlertEvent(
                    id = "1",
                    timestamp = 1_700_000_000_000L,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Повітряна тривога",
                    messageText = "Повітряна тривога в Київській та Харківській областях. Просимо негайно зайти у найближче укриття.",
                    matchedKeyword = "тривога",
                ),
            ),
            onAction = {},
        )
    }
}
