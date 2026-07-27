package com.revakovskyi.vartovyi.ui.screen.legal.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun LegalConsentTexts(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.legal_consent_title),
            style = VartovyiTheme.typography.headlineSmall,
            color = VartovyiTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Surface(
            color = VartovyiTheme.colors.errorContainer,
            shape = VartovyiTheme.shapes.large,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(VartovyiTheme.spacing.standard)
            ) {
                Text(
                    text = stringResource(R.string.legal_consent_important_label),
                    style = VartovyiTheme.typography.labelLarge,
                    color = VartovyiTheme.colors.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

                Text(
                    text = stringResource(R.string.legal_consent_disclaimer),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text(
            text = stringResource(R.string.legal_consent_description),
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(name = "Legal consent texts", widthDp = 360)
@Composable
private fun LegalConsentTextsPreview() {
    VartovyiTheme {
        LegalConsentTexts()
    }
}
