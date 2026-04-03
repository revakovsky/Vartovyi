package com.revakovskyi.vartovyi.ui.screen.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
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
    val titleText = stringResource(R.string.legal_consent_title)
    val descriptionText = stringResource(R.string.legal_consent_description)
    val privacyButtonText = stringResource(R.string.legal_consent_open_privacy)
    val termsButtonText = stringResource(R.string.legal_consent_open_terms)
    val confirmButtonText = stringResource(R.string.legal_consent_confirm)
    val refuseButtonText = stringResource(R.string.legal_consent_refuse)

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
            .fillMaxSize()
            .background(VartovyiTheme.colors.background)
            .padding(
                horizontal = VartovyiTheme.spacing.medium,
                vertical = VartovyiTheme.spacing.massive,
            ),
    ) {
        Text(
            text = titleText,
            style = VartovyiTheme.typography.headlineSmall,
            color = VartovyiTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
        )

        Text(
            text = descriptionText,
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        VartovyiActionButton(
            text = privacyButtonText,
            onClick = { onAction(LegalConsentUiContract.Action.OpenPrivacyPolicy) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = !state.isLoading,
            contentColor = VartovyiTheme.colors.onPrimary,
            borderColor = VartovyiTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        VartovyiActionButton(
            text = termsButtonText,
            onClick = { onAction(LegalConsentUiContract.Action.OpenTermsOfUse) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = !state.isLoading,
            contentColor = VartovyiTheme.colors.onPrimary,
            borderColor = VartovyiTheme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        VartovyiActionButton(
            text = confirmButtonText,
            onClick = { onAction(LegalConsentUiContract.Action.Confirm) },
            style = VartovyiActionButtonStyle.Filled,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        VartovyiActionButton(
            text = refuseButtonText,
            onClick = { onAction(LegalConsentUiContract.Action.Refuse) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = !state.isLoading,
            contentColor = VartovyiTheme.colors.onPrimary,
            borderColor = VartovyiTheme.colors.error,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        )
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
