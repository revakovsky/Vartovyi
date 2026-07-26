package com.revakovskyi.vartovyi.ui.screen.legal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun LegalConsentDocumentButtons(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onAction: (action: LegalConsentUiContract.Action) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
    ) {
        VartovyiActionButton(
            text = stringResource(R.string.legal_consent_open_privacy),
            onClick = { onAction(LegalConsentUiContract.Action.OpenPrivacyPolicy) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = isEnabled,
            borderColor = VartovyiTheme.colors.primary,
        )

        VartovyiActionButton(
            text = stringResource(R.string.legal_consent_open_terms),
            onClick = { onAction(LegalConsentUiContract.Action.OpenTermsOfUse) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = isEnabled,
            borderColor = VartovyiTheme.colors.primary,
        )
    }
}

@Composable
internal fun LegalConsentDecisionButtons(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onAction: (action: LegalConsentUiContract.Action) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
    ) {
        VartovyiActionButton(
            text = stringResource(R.string.legal_consent_confirm),
            onClick = { onAction(LegalConsentUiContract.Action.Confirm) },
            style = VartovyiActionButtonStyle.Filled,
            enabled = isEnabled,
        )

        VartovyiActionButton(
            text = stringResource(R.string.legal_consent_refuse),
            onClick = { onAction(LegalConsentUiContract.Action.Refuse) },
            style = VartovyiActionButtonStyle.Outlined,
            enabled = isEnabled,
            borderColor = VartovyiTheme.colors.error,
        )
    }
}

@Preview(name = "Legal consent buttons", widthDp = 360)
@Composable
private fun LegalConsentButtonsPreview() {
    VartovyiTheme {
        Column(verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard)) {
            LegalConsentDocumentButtons(
                isEnabled = true,
                onAction = {},
            )

            LegalConsentDecisionButtons(
                isEnabled = true,
                onAction = {},
            )
        }
    }
}
