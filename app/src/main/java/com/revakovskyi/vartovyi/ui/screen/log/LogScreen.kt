package com.revakovskyi.vartovyi.ui.screen.log

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
fun LogScreen(
    viewModel: LogViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is LogUiContract.Event.NavigateBack -> onNavigateBack()
            else -> Unit
        }
    }

    LogContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun LogContent(
    modifier: Modifier = Modifier,
    state: LogUiContract.State,
    onAction: (action: LogUiContract.Action) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Log entries: ${state.logEntries.size}",
            color = VartovyiTheme.colors.onBackground,
        )
    }
}

@Preview
@Composable
private fun LogContentPreview() {
    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(),
            onAction = {},
        )
    }
}
