package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.theme.bodyLink

@Composable
fun LegalDocumentsSettingsSection(
    modifier: Modifier = Modifier,
    onPrivacyPolicyClick: () -> Unit,
    onTermsOfUseClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = VartovyiTheme.spacing.medium)
    ) {
        Text(
            text = stringResource(R.string.legal_consent_open_privacy),
            style = VartovyiTheme.typography.bodyLink,
            color = VartovyiTheme.colors.primary,
            modifier = Modifier.clickable(onClick = onPrivacyPolicyClick)
        )

        Text(
            text = stringResource(R.string.legal_consent_open_terms),
            style = VartovyiTheme.typography.bodyLink,
            color = VartovyiTheme.colors.primary,
            modifier = Modifier.clickable(onClick = onTermsOfUseClick)
        )
    }
}

@Preview
@Composable
private fun LegalDocumentsSettingsSectionPreview() {
    VartovyiTheme {
        LegalDocumentsSettingsSection(
            onPrivacyPolicyClick = {},
            onTermsOfUseClick = {},
        )
    }
}
