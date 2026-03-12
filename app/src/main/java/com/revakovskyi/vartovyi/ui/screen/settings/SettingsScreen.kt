package com.revakovskyi.vartovyi.ui.screen.settings

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
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is SettingsUiContract.Event.NavigateBack -> onNavigateBack()
            else -> Unit
        }
    }

    SettingsContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiContract.State,
    onAction: (action: SettingsUiContract.Action) -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Settings",
            color = VartovyiTheme.colors.onBackground,
        )
    }
}

@Preview
@Composable
private fun SettingsContentPreview() {
    VartovyiTheme {
        SettingsContent(
            state = SettingsUiContract.State(),
            onAction = {},
        )
    }
}
