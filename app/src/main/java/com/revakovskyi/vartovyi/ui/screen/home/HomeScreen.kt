package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Home — ${state.monitoringState}",
            color = VartovyiTheme.colors.onBackground,
        )
    }
}

@Preview
@Composable
private fun HomeContentPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(),
            onAction = {},
        )
    }
}
