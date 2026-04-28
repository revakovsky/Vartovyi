package com.revakovskyi.vartovyi.ui.screen.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.ScrollProgressBar
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
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
    val scrollState = rememberScrollState()

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = VartovyiTheme.spacing.medium)
                        .padding(
                            top = VartovyiTheme.spacing.massive,
                            bottom = VartovyiTheme.spacing.standard,
                        )
                ) {
                    Text(
                        text = stringResource(R.string.legal_consent_title),
                        style = VartovyiTheme.typography.headlineSmall,
                        color = VartovyiTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = stringResource(R.string.legal_consent_description),
                        style = VartovyiTheme.typography.bodyLarge,
                        color = VartovyiTheme.colors.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        color = VartovyiTheme.colors.errorContainer,
                        shape = VartovyiTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(VartovyiTheme.spacing.standard)
                        ) {
                            Text(
                                text = stringResource(R.string.legal_consent_important_label),
                                style = VartovyiTheme.typography.labelLarge,
                                color = VartovyiTheme.colors.onErrorContainer,
                            )

                            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

                            Text(
                                text = stringResource(R.string.legal_consent_disclaimer),
                                style = VartovyiTheme.typography.bodyMedium,
                                color = VartovyiTheme.colors.onErrorContainer,
                            )
                        }
                    }

                    VartovyiActionButton(
                        text = stringResource(R.string.legal_consent_open_privacy),
                        onClick = { onAction(LegalConsentUiContract.Action.OpenPrivacyPolicy) },
                        style = VartovyiActionButtonStyle.Outlined,
                        enabled = !state.isLoading,
                        borderColor = VartovyiTheme.colors.primary,
                    )

                    VartovyiActionButton(
                        text = stringResource(R.string.legal_consent_open_terms),
                        onClick = { onAction(LegalConsentUiContract.Action.OpenTermsOfUse) },
                        style = VartovyiActionButtonStyle.Outlined,
                        enabled = !state.isLoading,
                        borderColor = VartovyiTheme.colors.primary,
                    )
                }

                ScrollProgressBar(
                    scrollState = scrollState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = VartovyiTheme.spacing.medium)
                    .padding(bottom = VartovyiTheme.spacing.medium)
            ) {
                VartovyiActionButton(
                    text = stringResource(R.string.legal_consent_confirm),
                    onClick = { onAction(LegalConsentUiContract.Action.Confirm) },
                    style = VartovyiActionButtonStyle.Filled,
                    enabled = !state.isLoading,
                )

                VartovyiActionButton(
                    text = stringResource(R.string.legal_consent_refuse),
                    onClick = { onAction(LegalConsentUiContract.Action.Refuse) },
                    style = VartovyiActionButtonStyle.Outlined,
                    enabled = !state.isLoading,
                    borderColor = VartovyiTheme.colors.error,
                )
            }
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
