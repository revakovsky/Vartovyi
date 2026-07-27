package com.revakovskyi.vartovyi.ui.screen.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.ui.screen.legal.components.LegalConsentLandscapeContent
import com.revakovskyi.vartovyi.ui.screen.legal.components.LegalConsentPortraitContent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.openCustomChromeTab
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun LegalConsentScreen(
    viewModel: LegalConsentViewModel = koinViewModel(),
    onRefuse: () -> Unit,
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is LegalConsentUiContract.Event.OpenUrl -> {
                openCustomChromeTab(context = context, url = event.url)
            }

            is LegalConsentUiContract.Event.CloseApplication -> onRefuse()
        }
    }

    LegalConsentContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun LegalConsentContent(
    modifier: Modifier = Modifier,
    state: LegalConsentUiContract.State,
    onAction: (action: LegalConsentUiContract.Action) -> Unit,
) {
    val windowSize = LocalWindowInfo.current.containerSize

    val scrollState = rememberScrollState()
    val actionsScrollState = rememberScrollState()

    val isTwoPaneLayout = windowSize.width > windowSize.height

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        if (isTwoPaneLayout) {
            LegalConsentLandscapeContent(
                isEnabled = !state.isLoading,
                scrollState = scrollState,
                actionsScrollState = actionsScrollState,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LegalConsentPortraitContent(
                isEnabled = !state.isLoading,
                scrollState = scrollState,
                onAction = onAction,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
private fun LegalConsentContentPreview() {
    VartovyiTheme {
        LegalConsentContent(
            state = LegalConsentUiContract.State(
                isLoading = false,
                isAccepted = false,
            ),
            onAction = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
